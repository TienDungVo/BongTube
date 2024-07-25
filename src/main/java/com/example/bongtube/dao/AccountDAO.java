package com.example.bongtube.dao;

import com.example.bongtube.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountDAO extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);
}
