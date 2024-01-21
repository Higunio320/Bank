package com.bank.api.transfer;

import com.bank.api.transfer.data.TransferListResponse;
import com.bank.api.transfer.data.TransferRequest;
import com.bank.api.transfer.data.TransferResponse;
import com.bank.api.transfer.interfaces.TransferService;
import com.bank.entities.account.Account;
import com.bank.entities.account.interfaces.AccountRepository;
import com.bank.entities.transfer.Transfer;
import com.bank.entities.transfer.interfaces.TransferMapper;
import com.bank.entities.transfer.interfaces.TransferRepository;
import com.bank.entities.user.User;
import com.bank.utils.exceptions.account.AccountNotFoundException;
import com.bank.utils.exceptions.request.IncorrectRequestException;
import com.bank.utils.exceptions.transfer.IncorrectTransferException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final TransferMapper transferMapper;

    private static final int PAGE_SIZE = 10;
    private static final String USER_DOES_NOT_HAVE_AN_ACCOUNT_MESSAGE = "You don't have an account";
    private static final String INCORRECT_RECEIVER_ACCOUNT_NUMBER_MESSAGE = "Incorrect receiver account number";

    @Override
    public final TransferListResponse getTransfersForUserOnPage(User user, int pageNumber) {
        if(pageNumber < 0) {
            throw new IncorrectRequestException("Page number cannot be less than 0");
        }

        log.info("Getting transfers for user: {} on page: {}", user.getUsername(), pageNumber);

        Account account = accountRepository.getAccountByUser(user)
                .orElseThrow(() -> new AccountNotFoundException(USER_DOES_NOT_HAVE_AN_ACCOUNT_MESSAGE));

        String accountNumber = account.getAccountNumber();

        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE);
        Page<Transfer> transferPage = transferRepository.getAllForUser(accountNumber, pageable);

        long totalTransfers = transferPage.getTotalElements();

        log.info("Returning page of {} transfers, number of al transfers: {}",
                transferPage.getNumberOfElements(), totalTransfers);

        List<TransferResponse> transferList = transferPage.stream()
                .map(transferMapper::transferToTransferResponse)
                .toList();

        return TransferListResponse.builder()
                .transferList(transferList)
                .totalTransfers(totalTransfers)
                .build();
    }

    @Override
    public final TransferResponse makeATransfer(User user, TransferRequest transferRequest) {
        log.info("Creating a transfer for user: {} for request: {}", user.getUsername(), transferRequest);

        Account senderAccount = accountRepository.getAccountByUser(user)
                .orElseThrow(() -> new AccountNotFoundException(USER_DOES_NOT_HAVE_AN_ACCOUNT_MESSAGE));

        String senderAccountNumber = senderAccount.getAccountNumber();

        String receiverAccountNumber = transferRequest.receiverAccountNumber();

        Account receiverAccount = accountRepository.getAccountByAccountNumber(receiverAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException(INCORRECT_RECEIVER_ACCOUNT_NUMBER_MESSAGE));

        double amount = transferRequest.amount();

        if(amount <= 0.0) {
            throw new IncorrectTransferException("Amount cannot be less or equal to 0");
        }

        if(!senderAccount.sendTransfer(amount)) {
            throw new IncorrectTransferException("You don't have enough money to make that transfer");
        }

        if(!receiverAccount.receiveTransfer(amount)) {
            throw new IncorrectTransferException("Receiver has too much money to accept that transfer");
        }

        Transfer transfer = Transfer.builder()
                .senderAccountNumber(senderAccountNumber)
                .receiverAccountNumber(receiverAccountNumber)
                .receiverName(transferRequest.receiverName())
                .title(transferRequest.title())
                .transferDate(Instant.now())
                .amount(amount)
                .build();

        log.info("Saving sender: {} and receiver: {} accounts and transfer", senderAccountNumber, receiverAccountNumber);

        Transfer savedTransfer = transferRepository.save(transfer);

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);


        return transferMapper.transferToTransferResponse(savedTransfer);
    }
}
