package com.bank.config.jwt;

import com.bank.api.auth.constants.AuthControllerConstants;
import com.bank.config.jwt.interfaces.JwtService;
import com.bank.entities.invalidtoken.interfaces.InvalidTokenRepository;
import com.bank.utils.exceptions.BankException;
import com.bank.utils.exceptions.BankExceptionHandler;
import com.bank.utils.exceptions.BankExceptionResponse;
import com.bank.utils.exceptions.authorization.BadCredentialsException;
import com.bank.utils.exceptions.tokens.ExpiredTokenException;
import com.bank.utils.exceptions.tokens.InvalidTokenException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final BankExceptionHandler bankExceptionHandler;
    private final ObjectMapper objectMapper;
    private final InvalidTokenRepository invalidTokenRepository;

    private static final String AUTH_MAPPING = AuthControllerConstants.AUTH_API_MAPPING;

    @Override
    protected final void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String jwt;
        String userLogin;

        if (authHeader == null || !authHeader.startsWith("Bearer ") || request.getRequestURI().startsWith(AUTH_MAPPING)) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Authenticating token");

        jwt = authHeader.substring(7);

        if(invalidTokenRepository.existsByToken(jwt)) {
            writeExceptionToHttpResponse(response, new ExpiredTokenException());

            filterChain.doFilter(request, response);
            return;
        }

        try {
            userLogin = jwtService.extractUsernameFromSubject(jwt);
        } catch (RuntimeException ignored) {
            writeExceptionToHttpResponse(response, new InvalidTokenException());

            filterChain.doFilter(request, response);
            return;
        }

        if(userLogin != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails;
            try {
                userDetails = userDetailsService.loadUserByUsername(userLogin);
            } catch (BadCredentialsException ignored) {
                writeExceptionToHttpResponse(response, new InvalidTokenException());

                filterChain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.info("User: {} authenticated", userLogin);
        }

        filterChain.doFilter(request, response);
    }

    private void writeExceptionToHttpResponse(HttpServletResponse response, BankException exception) throws IOException {
        ResponseEntity<BankExceptionResponse> responseEntity = bankExceptionHandler.handleBankException(exception);

        String jsonBody = objectMapper.writeValueAsString(responseEntity.getBody());
        response.getWriter().write(jsonBody);
        response.setContentType("/application/json");
    }

}
