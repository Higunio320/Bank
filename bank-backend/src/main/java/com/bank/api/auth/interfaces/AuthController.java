package com.bank.api.auth.interfaces;

import com.bank.api.auth.data.AuthenticationRequest;
import com.bank.api.auth.data.AuthenticationResponse;
import com.bank.api.auth.data.LogoutRequest;
import com.bank.api.auth.data.PasswordValidationRequest;
import com.bank.api.auth.data.TokenResponse;
import org.springframework.http.ResponseEntity;

public interface AuthController {

    ResponseEntity<AuthenticationResponse> generatePassword(AuthenticationRequest request);

    ResponseEntity<TokenResponse> authenticate(PasswordValidationRequest request);

    ResponseEntity<Object> logout(LogoutRequest logoutRequest);
}
