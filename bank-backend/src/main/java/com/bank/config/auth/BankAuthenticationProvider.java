package com.bank.config.auth;

import com.bank.entities.user.User;
import com.bank.entities.user.interfaces.UserRepository;
import com.bank.entities.user.password.Password;
import com.bank.utils.exceptions.authorization.BadCredentialsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BankAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public final Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if(!(authentication instanceof BankAuthentication bankAuthentication) || !(userDetails instanceof User user)) {
            throw new BadCredentialsException();
        }

        List<Integer> indexes = bankAuthentication.getIndexes();

        List<Password> passwords = user.getPasswords();


        for(Password passwordObj : passwords) {
            if(passwordObj.getWhichChars().stream().toList().equals(indexes)) {
                if(passwordEncoder.matches(password, passwordObj.getHash())) {
                    return new BankAuthentication(userDetails, password, indexes);
                }
                log.info("Password does not match");
                throw new BadCredentialsException();
            }
        }

        // only to prevent timing attack
        passwordEncoder.encode(password);

        log.info("None of the indexes matches");
        throw new BadCredentialsException();
    }

    @Override
    public final boolean supports(Class<?> authentication) {
        return authentication.equals(BankAuthentication.class);
    }
}
