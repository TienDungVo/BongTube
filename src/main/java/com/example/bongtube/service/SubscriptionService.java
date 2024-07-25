package com.example.bongtube.service;

import com.example.bongtube.dao.SubscriptionDAO;
import com.example.bongtube.dao.UserDAO;
import com.example.bongtube.entity.Subscription;
import com.example.bongtube.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    private UserDAO userRepository;

    @Autowired
    private SubscriptionDAO subscriptionRepository;

    @Autowired
    private SessionService sessionService;


    // Phương thức kiểm tra sự tồn tại của Subscription
    public boolean isSubscribed(Long subscribedToUserId) {
        String username = sessionService.get("username");

        Optional<User> subscriberOptional = userRepository.findByAccountUsername(username);
        if (subscriberOptional.isPresent()) {
            User subscriber = subscriberOptional.get();
            Optional<User> subscribedToOptional = userRepository.findById(Math.toIntExact(subscribedToUserId));

            if (subscribedToOptional.isPresent()) {
                User subscribedTo = subscribedToOptional.get();
                Optional<Subscription> subscriptionOptional = subscriptionRepository.findBySubscriberAndSubscribedTo(subscriber, subscribedTo);
                return subscriptionOptional.isPresent();
            }
        }

        return false;
    }
    public void subscribe(Long userId) {
        String username = sessionService.get("username");
        Optional<User> subscriberOpt = userRepository.findByAccountUsername(username);
        if (subscriberOpt.isPresent()) {
            User subscriber = subscriberOpt.get();
            Optional<User> subscribedToOpt = userRepository.findById(Math.toIntExact(userId));
            if (subscribedToOpt.isPresent()) {
                User subscribedTo = subscribedToOpt.get();
                Optional<Subscription> existingSubscription = subscriptionRepository.findBySubscriberAndSubscribedTo(subscriber, subscribedTo);
                if (existingSubscription.isEmpty()) {
                    Subscription subscription = new Subscription();
                    subscription.setSubscriber(subscriber);
                    subscription.setSubscribedTo(subscribedTo);
                    subscription.setCreatedAt(LocalDateTime.now());
                    subscriptionRepository.save(subscription);
                }
            }
        }
    }

    public void unsubscribe(Long userId) {
        String username = sessionService.get("username");
        Optional<User> subscriberOpt = userRepository.findByAccountUsername(username);
        if (subscriberOpt.isPresent()) {
            User subscriber = subscriberOpt.get();
            Optional<User> subscribedToOpt = userRepository.findById(Math.toIntExact(userId));
            if (subscribedToOpt.isPresent()) {
                User subscribedTo = subscribedToOpt.get();
                Optional<Subscription> existingSubscription = subscriptionRepository.findBySubscriberAndSubscribedTo(subscriber, subscribedTo);
                if (existingSubscription.isPresent()) {
                    subscriptionRepository.delete(existingSubscription.get());
                }
            }
        }
    }



}
