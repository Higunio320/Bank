package com.bank.api.account.interfaces;

import com.bank.api.account.data.BasicAccountInfoResponse;
import com.bank.api.account.data.SensitiveAccountInfoResponse;
import com.bank.entities.user.User;
import org.springframework.http.ResponseEntity;

public interface AccountService {

    ResponseEntity<BasicAccountInfoResponse> getBasicAccountInfo(User user);

    ResponseEntity<SensitiveAccountInfoResponse> getSensitiveAccountInfo(User user);

    void registerAccount(String accountNumber, String idNumber, String cardNumber, double balance, User user);
}
