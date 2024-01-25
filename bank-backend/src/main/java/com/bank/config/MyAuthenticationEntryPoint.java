package com.bank.config;

import com.bank.utils.exceptions.BankExceptionHandler;
import com.bank.utils.exceptions.BankExceptionResponse;
import com.bank.utils.exceptions.authorization.UnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final BankExceptionHandler bankExceptionHandler;

    @Override
    public final void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        if(!response.getContentType().startsWith("/application/json")) {
            ResponseEntity<BankExceptionResponse> responseEntity = bankExceptionHandler.handleBankException(new UnauthorizedException());

            String jsonBody = objectMapper.writeValueAsString(responseEntity.getBody());
            response.getWriter().write(jsonBody);
            response.setContentType("/application/json");
        }
        log.info("User unauthorized");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}
