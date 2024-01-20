package com.bank.entities.user.interfaces;

import com.bank.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);

    //so that hibernate will not lazy load passwords
    @Query("SELECT u FROM User u JOIN FETCH u.passwords WHERE u.login = :login")
    Optional<User> findByLoginWithPasswords(@Param("login") String login);
}
