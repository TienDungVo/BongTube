package com.example.bongtube.controller;

import com.example.bongtube.dao.*;
import com.example.bongtube.entity.*;
import com.example.bongtube.service.SessionService;
import com.example.bongtube.service.SubscriptionService;
import com.example.bongtube.service.daoService.CommentService;
import com.example.bongtube.service.daoService.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user_video")
public class WatchController {
    @Autowired
    SessionService sessionService;
    @Autowired
    VideoService videoService;
    @Autowired
    AccountDAO accountDAO;
    @Autowired
    private VideoDAO videoDAO;
    @Autowired
    private LikeDislikeDAO likeDislikeDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private SubscriptionService subscriptionService;
  @Autowired
    CommentService commentService;


    @RequestMapping("/watch/{videoid}")
    public String watch(@PathVariable("videoid") String videoid, Model model) {
        Optional<Video> video = videoDAO.findById(Long.valueOf(videoid));
        if (video.isPresent()) {// Cập nhật số lượt xem
            videoService.incrementViews(Long.valueOf(videoid));
            model.addAttribute("video", video.get());
            model.addAttribute("videos", videoDAO.findAll());
            List<Comment> comments = commentService.getCommentsByVideoId(Long.valueOf(videoid));
            model.addAttribute("comments", comments);
            String username = sessionService.get("username");
            if (username != null) {
                Long userId = video.get().getUser().getUserId(); // ID của người dùng sở hữu video
                boolean isSubscribed = subscriptionService.isSubscribed(userId);
                model.addAttribute("isSubscribed", isSubscribed);
            } else {
                model.addAttribute("isSubscribed", false); // Nếu người dùng chưa đăng nhập, mặc định là chưa đăng ký
            }
        } else {
            model.addAttribute("error", "Video not found");
        }
        return "layout_user/video";
    }

//    @RequestMapping("/watch/{videoid}")
//    public String watch(@PathVariable("videoid") String videoid, Model model) {
//        model.addAttribute("videos", videoDAO.findAll());
//        Optional<Video> video = videoDAO.findById(Long.valueOf(videoid));
//        if (video.isPresent()) {
//            model.addAttribute("video", video.get());
//        }
//        return "layout_user/video";
//      }

    @PostMapping("/like/{videoid}")
    public String likeVideo(@PathVariable("videoid") Long videoId, Model model) {
        String username = sessionService.get("username");
        System.out.println("Username from session: " + username);

        Optional<Account> accountOptional = accountDAO.findByUsername(username);

        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            System.out.println("Account found: " + account.getUsername());

            // Tìm User từ username của Account
            Optional<User> userOptional = userDAO.findByAccountUsername(username);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                Optional<Video> videoOptional = videoDAO.findById(videoId);

                if (videoOptional.isPresent()) {
                    Video video = videoOptional.get();

                    // Kiểm tra nếu người dùng đã like video này
                    Optional<LikeDislike> existingLike = likeDislikeDAO.findByUserAndVideoAndType(user, video, "like");
                    if (!existingLike.isPresent()) {
                        LikeDislike like = new LikeDislike();
                        like.setUser(user);
                        like.setVideo(video);
                        like.setType("like");
                        like.setCreatedAt(LocalDateTime.now());
                        likeDislikeDAO.save(like);

                    }
                    model.addAttribute("video", video);
                    model.addAttribute("videos", videoDAO.findAll());
                    return "layout_user/video"; // Trả về tên view tương ứng
                } else {
                    // Xử lý trường hợp không tìm thấy video
                    return "redirect:/error";
                }
            } else {
                // Xử lý trường hợp không tìm thấy user
                return "redirect:/login"; // Hoặc trang lỗi
            }
        } else {
            // Xử lý trường hợp không tìm thấy account
            return "redirect:/login"; // Hoặc trang lỗi
        }
    }
    @RequestMapping("/trend")
    public String trend(Model model) {
        List<Video> topVideos = videoDAO.findTop10ByOrderByViewsCountDesc();
        model.addAttribute("topVideos", topVideos);
        return "layout_user/hotTrend";
    }

}
