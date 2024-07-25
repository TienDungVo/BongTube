package com.example.bongtube.service.daoService;

import com.example.bongtube.dao.AccountDAO;
import com.example.bongtube.dao.UserDAO;
import com.example.bongtube.dao.VideoDAO;
import com.example.bongtube.entity.Account;
import com.example.bongtube.entity.User;
import com.example.bongtube.entity.Video;
import com.example.bongtube.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class VideoService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private VideoDAO videoDAO;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private AccountDAO accountDAO;


    public void saveVideoForAccount(Video video, Account account) {
        Optional<User> userOptional = userDAO.findByAccount(account);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            video.setUser(user);
            videoDAO.save(video);
        } else {

            throw new RuntimeException("Không tìm thấy người dùng cho tài khoản này");
        }
    }

    public void save(Video video) {
        videoDAO.save(video);
    }
    public void delete(Video video) {
        videoDAO.delete(video);
    }


    public List<Video> findVideosForAccount(Account account) {
        Optional<User> userOptional = userDAO.findByAccount(account);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return videoDAO.findByUser(user);
        }
        return Collections.emptyList(); // Trả về danh sách rỗng nếu không tìm thấy người dùng
    }

    public Optional<Video> findVideoById(Long videoId) {
        return videoDAO.findById(videoId);
    }
    public void incrementViews(Long videoId) {
        // Tìm video theo ID
        Video video = videoDAO.findById(videoId).orElse(null);

        if (video != null) {
            // Kiểm tra và đặt giá trị mặc định nếu cần
            if (video.getViewsCount() == null) {
                video.setViewsCount(0L);
            }

            // Tăng số lượt xem lên 1
            video.setViewsCount(video.getViewsCount() + 1);

            // Lưu cập nhật vào cơ sở dữ liệu
            videoDAO.save(video);
        }
    }



}
