package com.bank.utils.exceptions.authorization;

import com.bank.utils.exceptions.BankException;
import org.springframework.http.HttpStatus;

import java.io.Serial;

public class UnauthorizedException extends BankException {

    @Serial
    private static final long serialVersionUID = 5677469959766667395L;

    private static final String MESSAGE = "Unauthorized";

    public UnauthorizedException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }
}
