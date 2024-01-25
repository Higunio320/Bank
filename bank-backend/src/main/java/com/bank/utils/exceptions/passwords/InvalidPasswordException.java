package com.bank.utils.exceptions.passwords;

import com.bank.utils.exceptions.BankException;
import org.springframework.http.HttpStatus;

import java.io.Serial;

public class InvalidPasswordException extends BankException {

    @Serial
    private static final long serialVersionUID = 4009000823856896296L;

    private static final String MESSAGE = "Password must have at least 15 characters, max 30 characters" +
            "and have at least 1 lowercase letter, uppercase letter, digit and special sign" +
            "(!@#$%^&*()-_+={}[]:;\"\\|<>,.?/)";


    public InvalidPasswordException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
