package com.example.bongtube.controller;

import com.example.bongtube.dao.AccountDAO;
import com.example.bongtube.dao.SubscriptionDAO;
import com.example.bongtube.dao.UserDAO;
import com.example.bongtube.entity.Account;
import com.example.bongtube.entity.Subscription;
import com.example.bongtube.entity.User;
import com.example.bongtube.service.SessionService;
import com.example.bongtube.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/sub")
public class SubController {

    @Autowired
    private AccountDAO accountDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private SessionService sessionService;

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping("/subscribe")
    public String subscribe(@RequestParam String subscribedToId, @RequestParam String idvideowatch, RedirectAttributes redirectAttributes) {
        Long subscribedToUserId;
        try {
            subscribedToUserId = Long.parseLong(subscribedToId);
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("message", "error");
            redirectAttributes.addFlashAttribute("text", "Invalid user ID format");
            return "redirect:/user_video/watch/" + idvideowatch;
        }

        boolean isSubscribed = subscriptionService.isSubscribed(subscribedToUserId);

        if (isSubscribed) {
            // Nếu đã đăng ký, thực hiện hủy đăng ký
            subscriptionService.unsubscribe(subscribedToUserId);
            redirectAttributes.addFlashAttribute("message", "success");
            redirectAttributes.addFlashAttribute("text", "Hủy đăng ký thành công!");
        } else {
            // Nếu chưa đăng ký, thực hiện đăng ký
            subscriptionService.subscribe(subscribedToUserId);
            redirectAttributes.addFlashAttribute("message", "success");
            redirectAttributes.addFlashAttribute("text", "Đăng ký thành công!");
        }

        return "redirect:/user_video/watch/" + idvideowatch;
    }

}
