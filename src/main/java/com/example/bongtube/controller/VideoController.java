package com.example.bongtube.controller;

import com.example.bongtube.dao.AccountDAO;
import com.example.bongtube.dao.NotificationDAO;
import com.example.bongtube.entity.Account;
import com.example.bongtube.entity.Video;
import com.example.bongtube.service.SessionService;
import com.example.bongtube.service.daoService.NotificationService;
import com.example.bongtube.service.daoService.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/video")
public class VideoController {
    @Autowired
    SessionService sessionService;
    @Autowired
    VideoService videoService;
    @Autowired
    AccountDAO accountDAO;
    @Autowired
    NotificationDAO  notificationDAO;
    @Autowired
    private NotificationService notificationService;

    @RequestMapping("/insert")
    public String insert_video(Model model) {
        String username = sessionService.get("username"); // Lấy tên người dùng từ session
        Optional<Account> accountOptional = accountDAO.findByUsername(username);

        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            List<Video> videos = videoService.findVideosForAccount(account);
            model.addAttribute("videos", videos);
            Video video = new Video();
            model.addAttribute("video", video);
            return "user_manager/create_video";
        } else {
            return "layout_user/login";
        }


    }

    @RequestMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        String username = sessionService.get("username"); // Lấy tên người dùng từ session
        Optional<Account> accountOptional = accountDAO.findByUsername(username);
        Account account = accountOptional.get();
        List<Video> videos = videoService.findVideosForAccount(account);
        model.addAttribute("videos", videos);
        Optional<Video> videoOptional = videoService.findVideoById(id);

        if (videoOptional.isPresent()) {
            Video video = videoOptional.get();
            model.addAttribute("video", video); // Đưa đối tượng video vào model
            return "user_manager/create_video";
        } else {
            // Xử lý khi không tìm thấy video
            return "layout_user/login";
        }
    }


    @PostMapping("/add")
    public String addVideo(@ModelAttribute("video") Video video,
                           @RequestParam("thumbnailFile") MultipartFile photo) {
        String username = sessionService.get("username");
        Optional<Account> accountOptional = accountDAO.findByUsername(username);

        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();

            try {
                if (!photo.isEmpty()) {
                    // Save image file
                    String fileName = photo.getOriginalFilename();
                    Path imagesDir = Paths.get("").toAbsolutePath().resolve("src/main/resources/static/images");

                    if (!Files.exists(imagesDir)) {
                        Files.createDirectories(imagesDir);
                    }

                    try (InputStream photoInputStream = photo.getInputStream()) {
                        Files.copy(photoInputStream, imagesDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("Lưu ảnh thành công");

                        // Update video thumbnail URL
                        video.setThumbnailUrl("/images/" + fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Lỗi khi lưu ảnh");
                        return "user_manager/create_video"; // Redirect to error page or form
                    }
                }

                // Save video for the account
                videoService.saveVideoForAccount(video, account);
                notificationService.notifyFollowers(video);
                System.out.println("Thêm video thành công");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Lỗi khi thêm video: " + e.getMessage());
                return "user_manager/create_video"; // Redirect to error page or form
            }
        } else {
            System.out.println("Không tìm thấy tài khoản người dùng");
            return "user_manager/create_video"; // Redirect to error page or form
        }

        return "redirect:/video/insert"; // Redirect to video list or success page
    }

    // Mapping for updating an existing video
    @PostMapping("/update")
    public String updateVideo(@ModelAttribute("video") Video updatedVideo, @RequestParam("idvideo") String idvideo, @RequestParam("thumbnailFile") MultipartFile photo) {
        try {
            Long videoId = Long.parseLong(idvideo);
            Optional<Video> videoOptional = videoService.findVideoById(videoId);

            if (videoOptional.isPresent()) {
                Video existingVideo = videoOptional.get();

                // Cập nhật thông tin video
                existingVideo.setTitle(updatedVideo.getTitle());
                existingVideo.setDescription(updatedVideo.getDescription());
                existingVideo.setUrl(updatedVideo.getUrl());
                existingVideo.setDuration(updatedVideo.getDuration());
                existingVideo.setStatus(updatedVideo.getStatus());

                // Cập nhật hình ảnh đại diện nếu có thay đổi
                if (!photo.isEmpty()) {
                    String fileName = photo.getOriginalFilename();
                    Path imagesDir = Paths.get("").toAbsolutePath().resolve("src/main/resources/static/images");

                    if (!Files.exists(imagesDir)) {
                        Files.createDirectories(imagesDir);
                    }

                    try (InputStream photoInputStream = photo.getInputStream()) {
                        Files.copy(photoInputStream, imagesDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("Lưu ảnh thành công");

                        // Update video thumbnail URL
                        existingVideo.setThumbnailUrl("/images/" + fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Lỗi khi lưu ảnh");
                        return "user_manager/create_video"; // Redirect to error page or form
                    }
                }

                videoService.save(existingVideo);
                System.out.println("Cập nhật video thành công");
            } else {
                System.out.println("Không tìm thấy video");
                return "user_manager/create_video"; // Redirect to error page or form
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi khi cập nhật video: " + e.getMessage());
            return "user_manager/create_video"; // Redirect to error page or form
        }

        return "redirect:/video/insert"; // Redirect to video list or success page
    }

    // Mapping for deleting a video
    @PostMapping("/delete")
    public String deleteVideo(@RequestParam("idvideo") String idvideo) {
        try {
            Long videoId = Long.parseLong(idvideo);
            Optional<Video> videoOptional = videoService.findVideoById(videoId);

            if (videoOptional.isPresent()) {
                videoService.delete(videoOptional.get());
                System.out.println("Xóa video thành công");
            } else {
                System.out.println("Không tìm thấy video");
                return "user_manager/create_video"; // Redirect to error page or form
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi khi xóa video: " + e.getMessage());
            return "user_manager/create_video"; // Redirect to error page or form
        }

        return "redirect:/video/insert"; // Redirect to video list or success page
    }


}
