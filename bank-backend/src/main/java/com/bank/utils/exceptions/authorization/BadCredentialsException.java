package com.bank.utils.exceptions.authorization;

import com.bank.utils.exceptions.BankException;
import org.springframework.http.HttpStatus;

import java.io.Serial;

public class BadCredentialsException extends BankException {

    @Serial
    private static final long serialVersionUID = -8083694357401009580L;

    private static final String MESSAGE = "Bad credentials. Please make sure that you have entered correct login and password.";

    public BadCredentialsException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
