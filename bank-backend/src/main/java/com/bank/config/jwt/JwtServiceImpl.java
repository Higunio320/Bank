package com.bank.config.jwt;

import com.bank.config.jwt.interfaces.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    private static final String SECRET_KEY = System.getenv("JWT_KEY");
    private static final long AUTH_EXPIRATION_TIME = 1000L * 60L * 15L;
    private static final long LOGIN_EXPIRATION_TIME = 1000L * 60L * 2L;
    private static final String INDEXES_CLAIM = "indexes";
    private static final String USERNAME_CLAIM = "username";

    @Override
    public final String extractUsernameFromSubject(String token) {
        log.info("Extracting subject");
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public final String extractIndexes(String token) {
        log.info("Extracting indexes");
        return extractClaim(token, claims -> claims.get(INDEXES_CLAIM, String.class));
    }

    @Override
    public final String extractUsername(String token) {
        log.info("Extracting username");
        return extractClaim(token, claims -> claims.get(USERNAME_CLAIM, String.class));
    }

    @Override
    public final Instant extractExpirationDate(String token) {
        log.info("Extracting expiration date");
        return extractClaim(token, Claims::getExpiration).toInstant();
    }

    @Override
    public final String generateAuthToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, AUTH_EXPIRATION_TIME);
    }

    @Override
    public final String generateLoginToken(String username, String indexes) {
        Map<String, Object> claims = new HashMap<>(4);
        claims.put(INDEXES_CLAIM, indexes);
        claims.put(USERNAME_CLAIM, username);
        return generateToken(claims, null, LOGIN_EXPIRATION_TIME);
    }

    @Override
    public final boolean isTokenValid(String token, UserDetails userDetails) {
        return false;
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expirationTime) {
        Instant now = Instant.now();

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails != null ? userDetails.getUsername() : null)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationTime)))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(null, null, "Token expired");
        } catch (Exception e) {
            throw new RuntimeException("Invalid token");
        }
    }

    private Key getSignInKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
