package com.bank.utils.exceptions.request;

import com.bank.utils.exceptions.BankException;
import org.springframework.http.HttpStatus;

import java.io.Serial;

public class IncorrectRequestException extends BankException {


    @Serial
    private static final long serialVersionUID = 5852684888644594924L;

    public IncorrectRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
