package com.bank.utils.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class BankExceptionHandler {

    @ExceptionHandler({BankException.class})
    public final ResponseEntity<BankExceptionResponse> handleBankException(BankException bankException) {

        String message = bankException.getMessage();
        HttpStatus status = bankException.getStatus();
        String exceptionClassName = bankException.getClass().getSimpleName();
        LocalDateTime timeStamp = LocalDateTime.now();

        BankExceptionResponse exceptionResponse = buildExceptionResponse(exceptionClassName, message, timeStamp);

        log.error("Exception occurred with response: {}", exceptionResponse);

        return new ResponseEntity<>(exceptionResponse, status);
    }

    private BankExceptionResponse buildExceptionResponse(String exceptionClassName, String message, LocalDateTime timeStamp) {
        return BankExceptionResponse.builder()
                .exceptionName(exceptionClassName)
                .exceptionMessage(message)
                .timeStamp(timeStamp)
                .build();
    }
}
