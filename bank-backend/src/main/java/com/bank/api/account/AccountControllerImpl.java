package com.bank.api.account;

import com.bank.api.account.data.BasicAccountInfoResponse;
import com.bank.api.account.data.SensitiveAccountInfoResponse;
import com.bank.api.account.interfaces.AccountController;
import com.bank.api.account.interfaces.AccountService;
import com.bank.entities.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.bank.api.account.constants.AccountControllerConstants.ACCOUNT_API_MAPPING;
import static com.bank.api.account.constants.AccountControllerConstants.BASIC_ACCOUNT_INFO_MAPPING;
import static com.bank.api.account.constants.AccountControllerConstants.SENSITIVE_ACCOUNT_INFO_MAPPING;

@RestController
@RequestMapping(ACCOUNT_API_MAPPING)
@RequiredArgsConstructor
public class AccountControllerImpl implements AccountController {

    private final AccountService accountService;

    @Override
    @GetMapping(BASIC_ACCOUNT_INFO_MAPPING)
    public ResponseEntity<BasicAccountInfoResponse> getBasicAccountInfo(@AuthenticationPrincipal User user) {
        return accountService.getBasicAccountInfo(user);
    }

    @Override
    @GetMapping(SENSITIVE_ACCOUNT_INFO_MAPPING)
    public ResponseEntity<SensitiveAccountInfoResponse> getSensitiveAccountInfo(@AuthenticationPrincipal User user) {
        return accountService.getSensitiveAccountInfo(user);
    }
}
