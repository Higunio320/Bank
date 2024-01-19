package com.bank.utils.exceptions;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BankExceptionResponse(String exceptionName, String exceptionMessage, LocalDateTime timeStamp) {
}
