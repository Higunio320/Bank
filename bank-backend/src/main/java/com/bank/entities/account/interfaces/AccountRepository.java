package com.bank.entities.account.interfaces;

import com.bank.entities.account.Account;
import com.bank.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> getAccountByUser(User user);
}
