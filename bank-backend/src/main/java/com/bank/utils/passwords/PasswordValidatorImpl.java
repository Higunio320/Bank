package com.bank.utils.passwords;

import com.bank.utils.passwords.interfaces.PasswordValidator;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.regex.Pattern;

@Component
@NoArgsConstructor
@Slf4j
public class PasswordValidatorImpl implements PasswordValidator {

    private static final int MIN_PASSWORD_LENGTH = 15;
    private static final int MAX_PASSWORD_LENGTH = 30;
    private static final double MIN_PASSWORD_ENTROPY = 3.5;

    // pattern for at least 1 lowercase later, 1 uppercase letter, 1 number and 1 special character
    private Pattern pattern = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()\\-_+={\\[}\\]:;\"'\\\\|<,>.?/]).*$");



    @Override
    public boolean isValid(String password) {
        if(password == null) {
            log.warn("Null password");
            return false;
        }

        int length = password.length();

        if(length < MIN_PASSWORD_LENGTH || length > MAX_PASSWORD_LENGTH) {
            log.warn("Wrong password length");
            return false;
        }

        if(!this.pattern.matcher(password).matches()) {
            log.warn("Password does not match the pattern (at least 1 of lowercase, uppercase, number and special char");
            return false;
        }

        if(calculateEntropy(password) < MIN_PASSWORD_ENTROPY) {
            log.warn("Password entropy is too low");
            return false;
        }

        return true;
    }

    private double calculateEntropy(String text) {
        HashMap<Character, Integer> dictionary = new HashMap<>(text.length());

        for(char c : text.toCharArray()) {
            dictionary.put(c, dictionary.getOrDefault(c, 0) + 1);
        }

        double entropy = 0.0;

        for(Integer value: dictionary.values()) {
            double probability = (double) value / (double) text.length();
            entropy -= probability * Math.log(probability) / Math.log(2.0);
        }

        return entropy;

    }
}
