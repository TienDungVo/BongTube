package com.example.bongtube.dao;

import com.example.bongtube.entity.Account;
import com.example.bongtube.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserDAO extends JpaRepository<User, Integer> {
    Optional<User> findByAccount(Account account);
    Optional<User> findByAccountUsername(String username);
    @Query("SELECT s.subscriber FROM Subscription s WHERE s.subscribedTo = :user")
    List<User> findFollowersByUser(@Param("user") User user);
}
