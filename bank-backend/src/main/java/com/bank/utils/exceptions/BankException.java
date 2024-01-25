package com.bank.utils.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
public abstract class BankException extends RuntimeException {

    private HttpStatus status = HttpStatus.BAD_REQUEST;

    @Serial
    private static final long serialVersionUID = 6505185336933392103L;

    protected BankException(String message) {
        super(message);
    }

    protected BankException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
