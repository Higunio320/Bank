package com.bank.api.auth;

import com.bank.api.auth.data.AuthenticationRequest;
import com.bank.api.auth.data.AuthenticationResponse;
import com.bank.api.auth.data.PasswordValidationRequest;
import com.bank.api.auth.data.TokenResponse;
import com.bank.api.auth.interfaces.AuthService;
import com.bank.config.auth.BankAuthentication;
import com.bank.config.jwt.interfaces.JwtService;
import com.bank.entities.user.User;
import com.bank.entities.user.interfaces.UserRepository;
import com.bank.entities.user.password.Password;
import com.bank.entities.user.password.interfaces.PasswordRepository;
import com.bank.utils.exceptions.authorization.BadCredentialsException;
import com.bank.utils.passwords.interfaces.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordGenerator passwordGenerator;
    private final AuthenticationManager authenticationManager;
    private final PasswordRepository passwordRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int NUM_OF_PASSWORDS = 10;
    private static final int PASSWORD_LENGTH = 10;

    private static final String GETTING_USER_BY_LOGIN = "Getting user by login: {}";

    @Override
    public final AuthenticationResponse generatePassword(AuthenticationRequest request) {
        String username = request.username();

        log.info(GETTING_USER_BY_LOGIN, username);

        Optional<User> userOptional = userRepository.findByLogin(username);
        List<Integer> indexes;

        if(userOptional.isEmpty()) {
            log.info("User does not exist, generating dummy indexes");
            indexes = createDummyIndexes();
        } else {
            User user = userOptional.get();

            RandomGenerator random = new SecureRandom();

            List<Password> passwords = user.getPasswords();

            int passwordIndex = random.nextInt(passwords.size());

            indexes = passwords.get(passwordIndex).getWhichChars();
        }

        String indexesString = getIndexesString(indexes);

        log.info("Generating and returning password validation token for user: {}", username);
        String token = jwtService.generateLoginToken(request.username(), indexesString);

        return AuthenticationResponse.builder()
                .indexes(indexesString)
                .token(token)
                .build();
    }

    @Override
    public final TokenResponse authenticate(PasswordValidationRequest request) {
        String token = request.token();
        String password = request.password();

        String username;
        String indexesString;

        log.info("Retrieving username and indexes from token");
        try {
            username = jwtService.extractUsername(token);
            indexesString = jwtService.extractIndexes(token);
        } catch (RuntimeException e) {
            passwordEncoder.encode(password);
            throw new BadCredentialsException();
        }

        log.info(GETTING_USER_BY_LOGIN, username);

        User user;

        try {
            user = userRepository.findByLogin(username)
                    .orElseThrow(BadCredentialsException::new);
        } catch (BadCredentialsException e) {
            log.info("Invalid login");
            passwordEncoder.encode(password);
            throw e;
        }

        log.info("Getting indexes from string: {}", indexesString);
        List<Integer> indexes = getIndexes(indexesString);

        log.info("Authenticating user: {}", username);

        authenticationManager.authenticate(
                new BankAuthentication(
                        username,
                        password,
                        indexes
                )
        );

        String jwtToken = jwtService.generateAuthToken(user);

        log.info("Returning auth token");
        return TokenResponse.builder()
                .token(jwtToken)
                .build();
    }

    //only for the example data purposes
    @Override
    public void register(String username, String password) {
        List<Password> passwords = passwordGenerator.generatePasswords(password,
                NUM_OF_PASSWORDS, PASSWORD_LENGTH);

        passwordRepository.saveAll(passwords);

        User user = User.builder()
                .login(username)
                .passwords(passwords)
                .build();

        log.info("Saved user: {}", userRepository.save(user));
    }

    private String getIndexesString(List<Integer> indexes) {
        StringBuilder builder = new StringBuilder(indexes.size() * 2);

        builder.append(indexes.get(0));

        for(int i = 1; i < indexes.size(); i++) {
            builder.append(" ");
            builder.append(indexes.get(i));
        }

        return builder.toString();
    }

    private List<Integer> getIndexes(String indexesString) {

        String[] indexesArray = indexesString.split(" ");

        List<Integer> indexes = new ArrayList<>(indexesArray.length);

        for(String index: indexesArray) {
            try {
                indexes.add(Integer.parseInt(index));
            } catch (NumberFormatException e) {
                throw new BadCredentialsException();
            }
        }

        if(indexes.size() != PASSWORD_LENGTH) {
            throw new BadCredentialsException();
        }

        return indexes;
    }

    private List<Integer> createDummyIndexes() {
        int size = 10;
        List<Integer> indexes = new ArrayList<>(size);
        SecureRandom random = new SecureRandom();

        while(indexes.size() < size) {
            Integer index = random.nextInt(30);

            if(!indexes.contains(index)) {
                indexes.add(index);
            }
        }

        Collections.sort(indexes);

        return indexes;
    }
}
