package com.bank.api.auth.data;

import lombok.Builder;

@Builder
public record PasswordValidationRequest(String token, String password) {
}
