package com.bank.utils.exceptions.account;

import com.bank.utils.exceptions.BankException;
import org.springframework.http.HttpStatus;

import java.io.Serial;

public class AccountNotFoundException extends BankException {

    @Serial
    private static final long serialVersionUID = -4156195472569291582L;

    private static final String MESSAGE = "This user does not have an account";

    public AccountNotFoundException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
