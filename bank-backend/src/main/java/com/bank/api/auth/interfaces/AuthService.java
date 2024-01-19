package com.bank.api.auth.interfaces;

import com.bank.api.auth.data.AuthenticationRequest;
import com.bank.api.auth.data.AuthenticationResponse;
import com.bank.api.auth.data.PasswordValidationRequest;
import com.bank.api.auth.data.TokenResponse;

public interface AuthService {

    AuthenticationResponse generatePassword(AuthenticationRequest request);

    TokenResponse authenticate(PasswordValidationRequest request);

    void register(String username, String password);
}
