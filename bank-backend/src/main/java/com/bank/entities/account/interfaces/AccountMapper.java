package com.bank.entities.account.interfaces;

import com.bank.api.account.data.BasicAccountInfoResponse;
import com.bank.entities.account.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    BasicAccountInfoResponse accountToBasicInfo(Account account);
}
