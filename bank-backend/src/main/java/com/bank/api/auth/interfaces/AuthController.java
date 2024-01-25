package com.bank.api.auth.interfaces;

import com.bank.api.auth.data.UsernameRequest;
import com.bank.api.auth.data.AuthenticationResponse;
import com.bank.api.auth.data.LogoutRequest;
import com.bank.api.auth.data.PasswordWithTokenRequest;
import com.bank.api.auth.data.TokenResponse;
import org.springframework.http.ResponseEntity;

public interface AuthController {

    ResponseEntity<AuthenticationResponse> generatePassword(UsernameRequest request);

    ResponseEntity<TokenResponse> authenticate(PasswordWithTokenRequest request);

    ResponseEntity<Object> logout(LogoutRequest logoutRequest);

    ResponseEntity<Object> requestForPasswordResetMail(UsernameRequest usernameRequest);

    ResponseEntity<Object> passwordReset(PasswordWithTokenRequest request);
}
