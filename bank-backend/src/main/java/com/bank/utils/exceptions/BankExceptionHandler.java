package com.bank.utils.exceptions;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class BankExceptionHandler {

    private static final String EXCEPTION_OCCURRED_MESSAGE = "Exception occurred with response: {}";

    @ExceptionHandler(BankException.class)
    public final ResponseEntity<BankExceptionResponse> handleBankException(BankException bankException) {

        String message = bankException.getMessage();
        HttpStatus status = bankException.getStatus();
        String exceptionClassName = bankException.getClass().getSimpleName();
        LocalDateTime timeStamp = LocalDateTime.now();

        BankExceptionResponse exceptionResponse = buildExceptionResponse(exceptionClassName, message, timeStamp);

        log.error(EXCEPTION_OCCURRED_MESSAGE, exceptionResponse);

        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public final ResponseEntity<BankExceptionResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {

        String message = "Message could not have been handled";
        String exceptionClassName = exception.getClass().getSimpleName();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        LocalDateTime timeStamp = LocalDateTime.now();

        BankExceptionResponse exceptionResponse = buildExceptionResponse(exceptionClassName, message, timeStamp);

        log.error(EXCEPTION_OCCURRED_MESSAGE, exceptionResponse);

        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<BankExceptionResponse> handleConstraintViolationException(ConstraintViolationException exception) {

        String message = "Incorrect request";
        String exceptionClassName = exception.getClass().getSimpleName();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        LocalDateTime timeStamp = LocalDateTime.now();

        BankExceptionResponse exceptionResponse = buildExceptionResponse(exceptionClassName, message, timeStamp);

        log.error(EXCEPTION_OCCURRED_MESSAGE, exceptionResponse);

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
