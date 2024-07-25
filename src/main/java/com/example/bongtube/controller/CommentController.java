package com.example.bongtube.controller;

import com.example.bongtube.dao.AccountDAO;
import com.example.bongtube.dao.LikeDislikeDAO;
import com.example.bongtube.dao.UserDAO;
import com.example.bongtube.dao.VideoDAO;
import com.example.bongtube.entity.Account;
import com.example.bongtube.entity.Comment;
import com.example.bongtube.entity.User;
import com.example.bongtube.entity.Video;
import com.example.bongtube.service.SessionService;
import com.example.bongtube.service.daoService.CommentService;
import com.example.bongtube.service.daoService.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    SessionService sessionService;
    @Autowired
    VideoService videoService;
    @Autowired
    AccountDAO accountDAO;
    @Autowired
    UserDAO userDAO;



    @PostMapping("/add")
    public String addComment(@RequestParam Long videoId,
                             @RequestParam String content,
                             @RequestParam(required = false) Long parentCommentId) {
        // Lấy thông tin người dùng từ phiên làm việc
        String username = sessionService.get("username");
        System.out.println(username);
        Optional<Account> accountOptional = accountDAO.findByUsername(username);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            Optional<User> userOptional = userDAO.findByAccountUsername(username);

            if (userOptional.isPresent()) {
                User user = userOptional.get(); // Lấy đối tượng User liên quan đến tài khoản

                Comment comment = new Comment();
                comment.setVideo(new Video(videoId));
                comment.setUser(user); // Sử dụng đối tượng User từ cơ sở dữ liệu
                comment.setContent(content);
                comment.setCreatedAt(LocalDateTime.now());
                comment.setParentComment(parentCommentId != null ? new Comment(parentCommentId) : null);

                commentService.saveComment(comment);
            } else {
                // Xử lý trường hợp không tìm thấy User
                return "redirect:/error"; // Điều chỉnh theo đường dẫn chính xác của bạn
            }
        } else {
            // Xử lý trường hợp không tìm thấy Account
            return "redirect:/error"; // Điều chỉnh theo đường dẫn chính xác của bạn
        }

        return "redirect:/user_video/watch/" + videoId;
    }



    @DeleteMapping("/delete/{commentId}")
    public String deleteComment(@PathVariable Long commentId) {
        Optional<Comment> commentOptional = commentService.getCommentById(commentId);
        if (commentOptional.isPresent()) {
            Long videoId = commentOptional.get().getVideo().getVideoId();
            commentService.deleteComment(commentId);
            return "redirect:/user_video/watch/" + videoId;
        }
        return "redirect:/comments";
    }

    private Long getCurrentUserId() {
        // Implement logic to get the current user ID
        return 1L; // Example value
    }
}