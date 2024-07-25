package com.example.bongtube.dao;

import com.example.bongtube.entity.Notification;
import com.example.bongtube.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationDAO extends JpaRepository<Notification, Integer> {
    List<Notification> findByUser(User user);
}
