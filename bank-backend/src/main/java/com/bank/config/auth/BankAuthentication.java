package com.bank.config.auth;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.Serial;
import java.util.List;

@Getter
public class BankAuthentication extends UsernamePasswordAuthenticationToken {
    @Serial
    private static final long serialVersionUID = 6618256076782158721L;

    private final List<Integer> indexes;


    public BankAuthentication(Object principal, Object credentials, List<Integer> indexes) {
        super(principal, credentials);
        this.indexes = List.copyOf(indexes);
    }


    private void readObject(java.io.ObjectInputStream in) throws ClassNotFoundException, java.io.NotSerializableException {
        throw new java.io.NotSerializableException("BankAuthentication");
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.NotSerializableException {
        throw new java.io.NotSerializableException("BankAuthentication");
    }
}
