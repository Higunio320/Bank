package com.bank.utils.passwords;

import com.bank.entities.user.password.Password;
import com.bank.utils.passwords.interfaces.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PasswordGeneratorImpl implements PasswordGenerator {

    private static final double MAX_COMBINATION_PERCENTAGE = 0.4;

    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Password> generatePasswords(String password, int numberOfPasswords, int passwordLength) {
        if(!isCorrect(password.length(), numberOfPasswords, passwordLength)) {
            throw new IllegalArgumentException("Too many passwords to generate");
        }

        List<List<Integer>> indexList = new ArrayList<>(numberOfPasswords);
        List<String> passwords = new ArrayList<>(numberOfPasswords);
        SecureRandom random = new SecureRandom();

        while(indexList.size() < numberOfPasswords) {
            List<Integer> indexes = new ArrayList<>(passwordLength);

            while(indexes.size() < passwordLength) {
                int index = random.nextInt(password.length());

                if(!indexes.contains(index)) {
                    indexes.add(index);
                }
            }

            Collections.sort(indexes);

            if(!indexList.contains(indexes)) {
                indexList.add(indexes);
                String passwordToEncode = generatePassword(password, indexes);
                passwords.add(passwordEncoder.encode(passwordToEncode));
            }
        }

        List<Password> passwordList = new ArrayList<>(numberOfPasswords);

        for(int i = 0; i < numberOfPasswords; i++) {
            passwordList.add(Password.builder()
                    .hash(passwords.get(i))
                    .whichChars(indexList.get(i))
                    .build());
        }

        return passwordList;
    }

    private String generatePassword(String password, List<Integer> indexes) {
        StringBuilder passwordBuilder = new StringBuilder(indexes.size());

        for(Integer index: indexes) {
            passwordBuilder.append(password.charAt(index));
        }

        return passwordBuilder.toString();
    }

    private boolean isCorrect(int n, int numberOfPasswords, int passwordLength) {
        return numberOfPasswords < MAX_COMBINATION_PERCENTAGE * fastBinomialCoeff(n, passwordLength);
    }

    private long fastBinomialCoeff(int n, int k) {
        long result = 1L;

        if(k > n - k) {
            k = n - k;
        }

        for(int i = 0; i < k; ++i) {
            result *= (n - i);
            result /= (i + 1);
        }

        return result;
    }
}
