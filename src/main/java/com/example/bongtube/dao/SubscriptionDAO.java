package com.example.bongtube.dao;

import com.example.bongtube.entity.Subscription;
import com.example.bongtube.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionDAO extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findBySubscriberAndSubscribedTo(User subscriber, User subscribedTo);


}