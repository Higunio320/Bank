package com.bank.api.auth.data;

import lombok.Builder;

@Builder
public record UsernameRequest(String username) {
}
