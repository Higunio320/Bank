package com.bank.utils.exceptions.authorization;

import com.bank.utils.exceptions.BankException;
import org.springframework.http.HttpStatus;


public class UserBlockedException extends BankException {

    private static final String MESSAGE = "Too many attempts. Try again in %d days, %d hours, %d minutes, %d seconds. " +
            "If you don't know why your account is blocked, please contact bank";

    public UserBlockedException(long days, int hours, int minutes, int seconds) {
        super(String.format(MESSAGE, days, hours, minutes, seconds), HttpStatus.BAD_REQUEST);
    }
}
