package com.bank.api.auth.interfaces;

import com.bank.api.auth.data.UsernameRequest;
import com.bank.api.auth.data.AuthenticationResponse;
import com.bank.api.auth.data.PasswordWithTokenRequest;
import com.bank.api.auth.data.TokenResponse;

public interface AuthService {

    AuthenticationResponse generatePassword(UsernameRequest request);

    TokenResponse authenticate(PasswordWithTokenRequest request);

    void register(String username, String password);

    void logout(String token);

    void requestForPasswordResetMail(String username);

    void passwordReset(String token, String password);
}
