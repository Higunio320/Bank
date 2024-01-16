package com.bank.utils.encryption;

import com.bank.utils.encryption.interfaces.EncryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Base64;

@Service
@Slf4j
public class EncryptionServiceImpl implements EncryptionService {

    private static final String KEY_GEN_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 256;
    private static final String SALT = System.getenv("KEYGEN_SALT");

    private static final String ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    @Override
    public final String generateKey(String username) {
        KeySpec spec = new PBEKeySpec(username.toCharArray(),
                SALT.getBytes(StandardCharsets.UTF_8), ITERATION_COUNT, KEY_LENGTH);

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_GEN_ALGORITHM);

            byte[] hash = factory.generateSecret(spec).getEncoded();

            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            log.warn("Exception while generating key");
            log.warn(e.getMessage());
            return null;
        }
    }

    @Override
    public final String encrypt(String key, String text) {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        Key secretKey = new SecretKeySpec(decodedKey, "AES");
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            AlgorithmParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
            byte[] encryptedText = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            byte[] finalCipherText = new byte[iv.length + encryptedText.length];
            System.arraycopy(iv, 0, finalCipherText, 0, iv.length);
            System.arraycopy(encryptedText, 0, finalCipherText, iv.length, encryptedText.length);
            return Base64.getEncoder().encodeToString(finalCipherText);
        } catch (Exception e) {
            log.warn("Exception while encrypting text");
            log.warn(e.getMessage());
            return null;
        }
    }

    @Override
    public String decrypt(String key, String text) {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        Key secretKey = new SecretKeySpec(decodedKey, "AES");
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            byte[] encryptedText = Base64.getDecoder().decode(text);
            byte[] iv = new byte[IV_LENGTH];
            System.arraycopy(encryptedText, 0, iv, 0, iv.length);
            AlgorithmParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
            byte[] decryptedText = cipher.doFinal(encryptedText, iv.length, encryptedText.length - iv.length);
            return new String(decryptedText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("Exception while decrypting text");
            log.warn(e.getMessage());
            return null;
        }
    }
}
