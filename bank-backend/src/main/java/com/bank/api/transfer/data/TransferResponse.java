package com.bank.api.transfer.data;

import lombok.Builder;

import java.time.Instant;

@Builder
public record TransferResponse(String senderAccountNumber,
                               String receiverAccountNumber,
                               String receiverName,
                               String title,
                               Instant transferDate,
                               double amount) {
}
