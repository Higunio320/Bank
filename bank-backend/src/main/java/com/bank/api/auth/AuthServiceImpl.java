package com.bank.api.auth;

import com.bank.api.auth.data.UsernameRequest;
import com.bank.api.auth.data.AuthenticationResponse;
import com.bank.api.auth.data.PasswordWithTokenRequest;
import com.bank.api.auth.data.TokenResponse;
import com.bank.api.auth.interfaces.AuthService;
import com.bank.config.auth.BankAuthentication;
import com.bank.config.jwt.interfaces.JwtService;
import com.bank.entities.invalidtoken.InvalidToken;
import com.bank.entities.invalidtoken.interfaces.InvalidTokenRepository;
import com.bank.entities.user.User;
import com.bank.entities.user.interfaces.UserRepository;
import com.bank.entities.user.password.Password;
import com.bank.entities.user.password.interfaces.PasswordRepository;
import com.bank.utils.exceptions.authorization.BadCredentialsException;
import com.bank.utils.exceptions.authorization.UserBlockedException;
import com.bank.utils.exceptions.passwords.InvalidPasswordException;
import com.bank.utils.exceptions.tokens.ExpiredTokenException;
import com.bank.utils.exceptions.tokens.InvalidTokenException;
import com.bank.utils.passwords.interfaces.PasswordGenerator;
import com.bank.utils.passwords.interfaces.PasswordValidator;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

import static com.bank.config.constants.SecurityConfigConstants.ENV_FRONTEND_URL;
import static com.bank.config.constants.SecurityConfigConstants.FRONTEND_RESET_PASSWORD_LINK;

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
    private final InvalidTokenRepository invalidTokenRepository;
    private final PasswordValidator passwordValidator;

    private static final int NUM_OF_PASSWORDS = 10;
    private static final int PASSWORD_LENGTH = 8;

    private static final String GETTING_USER_BY_LOGIN = "Getting user by login: {}";
    private static final String SAVING_INVALID_TOKEN = "Saving token in expired tokens";
    private static final String TOKEN_ALREADY_USED = "Token has already been used";
    private static final String INVALID_TOKEN = "Invalid token";
    private static final String FRONTEND_URL = System.getenv(ENV_FRONTEND_URL);

    @Override
    public final AuthenticationResponse generatePassword(UsernameRequest request) {
        String username = request.username();

        log.info(GETTING_USER_BY_LOGIN, username);

        Optional<User> userOptional = userRepository.findByLogin(username);
        List<Integer> indexes;

        if(userOptional.isEmpty()) {
            log.info("User does not exist, generating dummy indexes");
            indexes = createDummyIndexes();
        } else {
            User user = userOptional.get();

            Instant unblockTime = user.getUnblockTime();

            if(unblockTime.isAfter(Instant.now())) {
                Duration duration = Duration.between(Instant.now(), unblockTime);
                throw new UserBlockedException(
                        duration.toDaysPart(),
                        duration.toHoursPart(),
                        duration.toMinutesPart(),
                        duration.toSecondsPart()
                );
            }

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
    public final TokenResponse authenticate(PasswordWithTokenRequest request) {
        String token = request.token();
        String password = request.password();

        checkIfInvalidTokenAndSaveIfNot(token);

        String username;
        String indexesString;

        log.info("Retrieving username and indexes from token");
        try {
            username = jwtService.extractUsername(token);
            indexesString = jwtService.extractIndexes(token);
        } catch (RuntimeException e) {
            log.info(INVALID_TOKEN);
            throw new InvalidTokenException();
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

        Instant unblockTime = user.getUnblockTime();

        if(unblockTime.isAfter(Instant.now())) {
            Duration duration = Duration.between(Instant.now(), unblockTime);
            throw new UserBlockedException(
                    duration.toDaysPart(),
                    duration.toHoursPart(),
                    duration.toMinutesPart(),
                    duration.toSecondsPart()
            );
        }

        log.info("Getting indexes from string: {}", indexesString);
        List<Integer> indexes = getIndexes(indexesString);

        log.info("Authenticating user: {}", username);

        try {
            authenticationManager.authenticate(
                    new BankAuthentication(
                            username,
                            password,
                            indexes
                    )
            );
        } catch (Exception ignored) {
            user.increaseIncorrectLoginAttempts();
            userRepository.save(user);
            throw new BadCredentialsException();
        }

        log.info("User authenticated, resetting login attempts");
        user.resetIncorrectLoginAttempts();
        userRepository.save(user);

        String jwtToken = jwtService.generateAuthToken(user);

        log.info("Returning auth token");
        return TokenResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public final void logout(String token) {
        log.info("Logging out user");

        if(invalidTokenRepository.existsByToken(token)) {
            log.info(TOKEN_ALREADY_USED);
            throw new ExpiredTokenException();
        }

        log.info("Getting username and expiration date from token");

        String username;
        Instant expirationDate;

        try {
            username = jwtService.extractUsernameFromSubject(token);
            expirationDate = jwtService.extractExpirationDate(token);
        } catch (ExpiredJwtException e) {
            log.info("Token already expired");
            return;
        } catch (RuntimeException e) {
            log.info(INVALID_TOKEN);
            throw new InvalidTokenException();
        }

        log.info("Logging out user with username: {}", username);

        InvalidToken expiredToken = InvalidToken.builder()
                .token(token)
                .expirationDate(expirationDate)
                .build();

        invalidTokenRepository.save(expiredToken);
    }

    @Override
    public final void requestForPasswordResetMail(String username) {
        log.info(GETTING_USER_BY_LOGIN, username);

        Optional<User> userOptional = userRepository.findByLogin(username);

        if(userOptional.isPresent()) {
            User user = userOptional.get();

            String passwordResetToken = jwtService.generatePasswordResetToken(username);

            String url = UriComponentsBuilder.fromUriString(
                    String.format("%s%s", FRONTEND_URL, FRONTEND_RESET_PASSWORD_LINK))
                    .queryParam("token", passwordResetToken)
                    .build().toUriString();

            log.info("Would send email to: {} with link: {}", user.getEmail(), url);
        } else {
            log.info("Username does not exist, not sending password reset link");
        }
    }

    @Override
    public final void passwordReset(String token, String password) {
        checkIfInvalidTokenAndSaveIfNot(token);

        log.info("Retrieving username from password reset token");
        String username;

        try {
            username = jwtService.extractUsernameForPasswordReset(token);
        } catch(RuntimeException ignored) {
            throw new InvalidTokenException();
        }

        if(!passwordValidator.isValid(password)) {
            throw new InvalidPasswordException();
        }

        log.info(GETTING_USER_BY_LOGIN, username);

        User user = userRepository.findByLoginWithPasswords(username)
                .orElseThrow(BadCredentialsException::new);

        List<Password> oldPasswords = user.getPasswords();

        log.info("Generating new passwords for user: {}", username);

        List<Password> newPasswords = passwordGenerator.generatePasswords(password,
                NUM_OF_PASSWORDS, PASSWORD_LENGTH);

        user.setPasswords(newPasswords);
        user.setLastPasswordChange(Instant.now());

        log.info("Saving user: {} with new passwords", username);

        passwordRepository.saveAll(newPasswords);

        userRepository.save(user);

        log.info("Deleting old passwords");

        passwordRepository.deleteAll(oldPasswords);
    }

    //only for the example data purposes
    @Override
    public final User register(String username, String password) {
        log.info("Registering user: {}", username);
        List<Password> passwords = passwordGenerator.generatePasswords(password,
                NUM_OF_PASSWORDS, PASSWORD_LENGTH);

        String email = String.format("%s%s", username, "@mail.pl");

        passwordRepository.saveAll(passwords);

        User user = User.builder()
                .login(username)
                .passwords(passwords)
                .email(email)
                .incorrectLoginAttempts(0)
                .unblockTime(Instant.now().minusSeconds(5L))
                .lastPasswordChange(Instant.now().minusSeconds(5L))
                .build();

        log.info("Saving user: {}", user);

        return userRepository.save(user);
    }

    private void checkIfInvalidTokenAndSaveIfNot(String token) {

        if(invalidTokenRepository.existsByToken(token)) {
            log.info(TOKEN_ALREADY_USED);
            throw new ExpiredTokenException();
        } else {
            try {
                log.info(SAVING_INVALID_TOKEN);
                Instant expirationDate = jwtService.extractExpirationDate(token);
                InvalidToken expiredToken = InvalidToken.builder()
                        .token(token)
                        .expirationDate(expirationDate)
                        .build();
                invalidTokenRepository.save(expiredToken);
            } catch(RuntimeException ignored) {
                throw new InvalidTokenException();
            }
        }
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
        List<Integer> indexes = new ArrayList<>(PASSWORD_LENGTH);
        SecureRandom random = new SecureRandom();

        while(indexes.size() < PASSWORD_LENGTH) {
            Integer index = random.nextInt(20);

            if(!indexes.contains(index)) {
                indexes.add(index);
            }
        }

        Collections.sort(indexes);

        return indexes;
    }
}
