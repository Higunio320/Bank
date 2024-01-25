package com.bank.utils.exceptions.tokens;

import com.bank.utils.exceptions.BankException;
import org.springframework.http.HttpStatus;

import java.io.Serial;

public class ExpiredTokenException extends BankException {

    @Serial
    private static final long serialVersionUID = -103081378153392993L;

    private static final String MESSAGE = "Token is expired";

    public ExpiredTokenException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
