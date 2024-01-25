package com.bank.api.transfer.interfaces;

import com.bank.api.transfer.data.TransferListResponse;
import com.bank.api.transfer.data.TransferRequest;
import com.bank.api.transfer.data.TransferResponse;
import com.bank.entities.user.User;

public interface TransferService {

    TransferListResponse getTransfersForUserOnPage(User user, int pageNumber);

    TransferResponse makeATransfer(User user, TransferRequest transferRequest);
}
