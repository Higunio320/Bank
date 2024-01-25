package com.bank.utils.passwords.interfaces;

import com.bank.entities.user.password.Password;

import java.util.List;

public interface PasswordGenerator {

    List<Password> generatePasswords(String password, int numberOfPasswords, int passwordLength);
}
