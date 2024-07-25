package com.example.bongtube.service.daoService;

import com.example.bongtube.dao.NotificationDAO;
import com.example.bongtube.dao.UserDAO;
import com.example.bongtube.entity.Notification;
import com.example.bongtube.entity.User;
import com.example.bongtube.entity.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationDAO notificationRepository;

    @Autowired
    private UserDAO userRepository;

    public void notifyFollowers(Video video) {
        User uploader = video.getUser();
        List<User> followers = userRepository.findFollowersByUser(uploader);

        for (User follower : followers) {
            Notification notification = new Notification();
            notification.setUser(follower);
            notification.setType("NEW_VIDEO");
            notification.setContent("Người bạn theo dõi " + uploader.getFullName() + " vừa đăng một video mới.");
            notification.setCreatedAt(LocalDateTime.now());
            notification.setReadStatus(false);
            notificationRepository.save(notification);
        }
    }
}
