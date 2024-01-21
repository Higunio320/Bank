package com.bank.api.account;

import com.bank.api.account.data.BasicAccountInfoResponse;
import com.bank.api.account.data.SensitiveAccountInfoResponse;
import com.bank.api.account.interfaces.AccountService;
import com.bank.entities.account.Account;
import com.bank.entities.account.interfaces.AccountMapper;
import com.bank.entities.account.interfaces.AccountRepository;
import com.bank.entities.user.User;
import com.bank.utils.encryption.interfaces.EncryptionService;
import com.bank.utils.exceptions.account.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final EncryptionService encryptionService;

    @Override
    public final ResponseEntity<BasicAccountInfoResponse> getBasicAccountInfo(User user) {
        log.info("Getting basic user info for user: {}", user.getUsername());

        Account account = accountRepository.getAccountByUser(user)
                .orElseThrow(AccountNotFoundException::new);

        log.info("Returning basic user info for user: {}", user.getUsername());

        return ResponseEntity.ok(accountMapper.accountToBasicInfo(account));
    }

    @Override
    public final ResponseEntity<SensitiveAccountInfoResponse> getSensitiveAccountInfo(User user) {
        log.info("Getting sensitive use info for user: {}", user.getUsername());

        Account account = accountRepository.getAccountByUser(user)
                .orElseThrow(AccountNotFoundException::new);

        String encryptionKey = encryptionService.generateKey(user.getUsername());

        String decryptedIdNumber = encryptionService.decrypt(encryptionKey, account.getIdNumber());
        String decryptedCardNumber = encryptionService.decrypt(encryptionKey, account.getCardNumber());

        SensitiveAccountInfoResponse response = SensitiveAccountInfoResponse.builder()
                .idNumber(decryptedIdNumber)
                .cardNumber(decryptedCardNumber)
                .build();

        log.info("Returning sensitive user info for user: {}", user.getUsername());

        return ResponseEntity.ok(response);
    }

    //only for example data purposes
    @Override
    public final void registerAccount(String accountNumber, String idNumber, String cardNumber, double balance, User user) {
        log.info("Creating account for user: {}", user.getUsername());

        String encryptionKey = encryptionService.generateKey(user.getUsername());

        String encryptedIdNumber = encryptionService.encrypt(encryptionKey, idNumber);
        String encryptedCardNumber = encryptionService.encrypt(encryptionKey, cardNumber);

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .balance(balance)
                .cardNumber(encryptedCardNumber)
                .idNumber(encryptedIdNumber)
                .user(user)
                .build();

        log.info("Saving account for user: {}", user.getUsername());

        accountRepository.save(account);
    }
}
