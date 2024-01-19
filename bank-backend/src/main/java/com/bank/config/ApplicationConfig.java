package com.bank.config;

import com.bank.config.auth.BankAuthenticationProvider;
import com.bank.entities.invalidtoken.interfaces.InvalidTokenRepository;
import com.bank.entities.user.interfaces.UserRepository;
import com.bank.utils.exceptions.authorization.BadCredentialsException;
import com.bank.utils.invalidtoken.TokenCleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class ApplicationConfig {

    private static final int BCRYPT_STRENGTH = 14;

    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            log.info("Getting user details by login: {}", username);
            return userRepository.findByLogin(username)
                    .orElseThrow(BadCredentialsException::new);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(new BankAuthenticationProvider(userDetailsService(), passwordEncoder())));
    }

    @Bean
    public TokenCleanup tokenCleanup(InvalidTokenRepository invalidTokenRepository) {
        return new TokenCleanup(invalidTokenRepository);
    }
}
