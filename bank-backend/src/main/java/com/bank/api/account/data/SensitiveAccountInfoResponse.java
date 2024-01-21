package com.bank.api.account.data;

import lombok.Builder;

@Builder
public record SensitiveAccountInfoResponse(String idNumber, String cardNumber) {
}
