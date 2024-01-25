package com.bank.utils.encryption.interfaces;

public interface EncryptionService {

    String generateKey(String username);

    String encrypt(String key, String text);

    String decrypt(String key, String text);
}
