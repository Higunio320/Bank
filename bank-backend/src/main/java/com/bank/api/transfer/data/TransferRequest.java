package com.bank.api.transfer.data;

import lombok.Builder;


@Builder
public record TransferRequest(String receiverAccountNumber,
                              String receiverName,
                              String title,
                              double amount) {
}
