package com.bank.utils.exceptions.account;

import com.bank.utils.exceptions.BankException;
import org.springframework.http.HttpStatus;

import java.io.Serial;

public class AccountNotFoundException extends BankException {

    @Serial
    private static final long serialVersionUID = -4156195472569291582L;

    public AccountNotFoundException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
