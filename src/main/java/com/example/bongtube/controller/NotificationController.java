package com.example.bongtube.controller;

import com.example.bongtube.dao.AccountDAO;
import com.example.bongtube.dao.NotificationDAO;
import com.example.bongtube.dao.UserDAO;
import com.example.bongtube.entity.Account;
import com.example.bongtube.entity.Notification;
import com.example.bongtube.entity.User;
import com.example.bongtube.service.SessionService;
import com.example.bongtube.service.daoService.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class NotificationController {

    @Autowired
    SessionService sessionService;
    @Autowired
    UserDAO userDAO;
    @Autowired
    AccountDAO accountDAO;
    @Autowired
    NotificationDAO notificationDAO;


    @GetMapping("/api/notifications")
    @ResponseBody
    public List<Notification> getNotifications(Model model) {
        String username = sessionService.get("username");
        Optional<Account> accountOptional = accountDAO.findByUsername(username);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            Optional<User> userOptional = userDAO.findByAccount(account);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                return notificationDAO.findByUser(user);
            }
        }
        return Collections.emptyList();
    }
}
