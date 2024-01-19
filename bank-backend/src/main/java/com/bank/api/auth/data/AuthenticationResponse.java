package com.bank.api.auth.data;

import lombok.Builder;

@Builder
public record AuthenticationResponse(String indexes, String token) {
}
