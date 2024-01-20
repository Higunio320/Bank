package com.bank.config.jwt.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;

public interface JwtService {

    String extractUsernameFromSubject(String token);

    String extractUsername(String token);

    String extractIndexes(String token);

    Instant extractExpirationDate(String token);

    String extractUsernameForPasswordReset(String token);

    String generateAuthToken(UserDetails userDetails);

    String generateLoginToken(String username, String indexes);

    String generatePasswordResetToken(String username);

    boolean isTokenValid(String token, UserDetails userDetails);
}
