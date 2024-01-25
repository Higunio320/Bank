package com.bank.utils.exceptions.transfer;

import com.bank.utils.exceptions.BankException;
import org.springframework.http.HttpStatus;

import java.io.Serial;

public class IncorrectTransferException extends BankException {

    @Serial
    private static final long serialVersionUID = -96005632516619274L;

    public IncorrectTransferException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
