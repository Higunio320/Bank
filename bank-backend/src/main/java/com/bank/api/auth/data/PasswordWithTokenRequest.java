package com.bank.api.auth.data;

import lombok.Builder;

@Builder
public record PasswordWithTokenRequest(String token, String password) {
}
