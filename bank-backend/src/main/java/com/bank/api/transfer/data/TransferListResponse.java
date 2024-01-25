package com.bank.api.transfer.data;

import lombok.Builder;

import java.util.List;

@Builder
public record TransferListResponse(List<TransferResponse> transferList, long totalTransfers) {
}
