package com.bank.utils.exceptions.tokens;

import com.bank.utils.exceptions.BankException;
import org.springframework.http.HttpStatus;

import java.io.Serial;

public class InvalidTokenException extends BankException {

    @Serial
    private static final long serialVersionUID = 8191933764820970505L;

    private static final String MESSAGE = "Invalid token";

    public InvalidTokenException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
