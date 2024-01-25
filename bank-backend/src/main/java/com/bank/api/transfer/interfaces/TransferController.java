package com.bank.api.transfer.interfaces;

import com.bank.api.transfer.data.TransferListResponse;
import com.bank.api.transfer.data.TransferRequest;
import com.bank.api.transfer.data.TransferResponse;
import com.bank.entities.user.User;
import org.springframework.http.ResponseEntity;

public interface TransferController {

    ResponseEntity<TransferListResponse> getTransfersForUserOnPage(User user, int pageNumber);

    ResponseEntity<TransferResponse> makeATransfer(User user, TransferRequest transferRequest);
}
