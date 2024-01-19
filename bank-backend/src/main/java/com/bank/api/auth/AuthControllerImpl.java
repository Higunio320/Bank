package com.bank.api.auth;

import com.bank.api.auth.data.AuthenticationRequest;
import com.bank.api.auth.data.AuthenticationResponse;
import com.bank.api.auth.data.LogoutRequest;
import com.bank.api.auth.data.PasswordValidationRequest;
import com.bank.api.auth.data.TokenResponse;
import com.bank.api.auth.interfaces.AuthController;
import com.bank.api.auth.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.bank.api.auth.constants.AuthControllerConstants.AUTHENTICATE_MAPPING;
import static com.bank.api.auth.constants.AuthControllerConstants.AUTH_API_MAPPING;
import static com.bank.api.auth.constants.AuthControllerConstants.GENERATE_PASSWORD_MAPPING;
import static com.bank.api.auth.constants.AuthControllerConstants.LOGOUT_MAPPING;

@RestController
@RequestMapping(AUTH_API_MAPPING)
@RequiredArgsConstructor
@Slf4j
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    @Override
    @PostMapping(GENERATE_PASSWORD_MAPPING)
    public final ResponseEntity<AuthenticationResponse> generatePassword(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.generatePassword(request));
    }

    @Override
    @PostMapping(AUTHENTICATE_MAPPING)
    public final ResponseEntity<TokenResponse> authenticate(@RequestBody PasswordValidationRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @Override
    @PostMapping(LOGOUT_MAPPING)
    public final ResponseEntity<Object> logout(@RequestBody LogoutRequest logoutRequest) {
        authService.logout(logoutRequest.token());
        return ResponseEntity.ok().build();
    }
}
