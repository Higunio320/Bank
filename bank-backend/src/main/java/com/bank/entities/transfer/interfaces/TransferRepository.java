package com.bank.entities.transfer.interfaces;

import com.bank.entities.transfer.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    @Query("SELECT t from Transfer t WHERE (t.senderAccountNumber = :accountNumber OR t.receiverAccountNumber = :accountNumber) ORDER BY t.transferDate DESC")
    Page<Transfer> getAllForUser(@Param("accountNumber") String accountNumber, Pageable pageable);
}
