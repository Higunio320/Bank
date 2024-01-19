package com.bank.entities.user.password.interfaces;

import com.bank.entities.user.password.Password;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordRepository extends JpaRepository<Password, Long> {
}
