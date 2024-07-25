package com.example.bongtube.controller;

import com.example.bongtube.dao.AccountDAO;
import com.example.bongtube.dao.NotificationDAO;
import com.example.bongtube.dao.UserDAO;
import com.example.bongtube.dao.VideoDAO;
import com.example.bongtube.entity.Account;
import com.example.bongtube.entity.User;
import com.example.bongtube.entity.Video;
import com.example.bongtube.service.CookieService;
import com.example.bongtube.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class UserController {
    @Autowired
    AccountDAO accountDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    CookieService cookieService;
    @Autowired
    SessionService sessionService;
    @Autowired
    VideoDAO videoDAO;
    @Autowired
    NotificationDAO notificationDAO;


    @RequestMapping ("/bong/index")
    public String home(Model model) {
        List<Video> videos = videoDAO.findAll();
        Collections.shuffle(videos);
        model.addAttribute("videos", videos);
        return "index";
    }
    @RequestMapping("/bong/video")
    public String video() {
        return "layout_user/video";
    }

    @RequestMapping("/bong/login")
    public String login() {
        return "layout_user/login";
    }

    @RequestMapping("/bong/signup")
    public String signup() {
        return "layout_user/signup";
    }
    @RequestMapping("/bong/profiles")
    public String profiles(Model model) {
        String username = sessionService.get("username");
        Optional<Account> accountOptional = accountDAO.findByUsername(username);

        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            Optional<User> userOptional = userDAO.findByAccount(account);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                model.addAttribute("user", user);
                model.addAttribute("thongbaos", notificationDAO.findByUser(user));
                // Set avatar URL to a default if not provided by user
                if (user.getAvatarUrl() == null || user.getAvatarUrl().isEmpty()) {
                    model.addAttribute("avatarUrl", "https://mdbcdn.b-cdn.net/img/Photos/new-templates/bootstrap-chat/ava3.webp");
                } else {
                    model.addAttribute("avatarUrl", user.getAvatarUrl());
                }
            } else {
                // If user data not found, create a new User object
                User newUser = new User();  // Assuming User entity has default constructor
                model.addAttribute("user", newUser);
                model.addAttribute("avatarUrl", "https://mdbcdn.b-cdn.net/img/Photos/new-templates/bootstrap-chat/ava3.webp");
            }

            model.addAttribute("account", account);
            return "user_manager/profiles"; // Return your profile view template
        } else {
            return "layout_user/login"; // Redirect to login page if account not found
        }
    }


    @RequestMapping("/sunny/login")
    public String login(Model model, HttpSession session,
                        @RequestParam("username_AC") String username,
                        @RequestParam("password_AC") String password,
                        @RequestParam(value = "remember", required = false) String remember) {
        try {
            Optional<Account> optionalAccount = accountDAO.findByUsername(username);
            if (optionalAccount.isPresent()) {
                Account account = optionalAccount.get();
                if (account.getPassword().equals(password)) {
                    System.out.println("Login successful");
                    session.setAttribute("username", username);
                    model.addAttribute("username", username);
                    model.addAttribute("password", password);

                    if (remember != null) {
                        cookieService.add("username", username, 24 * 30); // Lưu cookie trong 30 ngày
                        cookieService.add("password", password, 24 * 30); // Lưu cookie trong 30 ngày
                    }
                    model.addAttribute("message", "success");
                    model.addAttribute("text", "Đăng nhập thành công!");
                    return "layout_user/login";
                } else {
                    System.out.println("Password does not match");
                    model.addAttribute("message", "error");
                    model.addAttribute("text", "Mật Khẩu không đúng!");
                    return "layout_user/login";
                }
            } else {
                model.addAttribute("message", "error");
                model.addAttribute("text", "Tên đăng nhâp không tồn tại!");
                return "layout_user/login";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "error");
            model.addAttribute("text", "Có lỗi sảy ra!");
            return "layout_user/login";
        }
    }

    @RequestMapping("/account/add")
    public String save(Model model, @ModelAttribute("account") Account account, @RequestParam("confirmPassword") String comfirmPass) {
     String password = account.getPassword();
     if (password.equals(comfirmPass)) {
         String username = account.getUsername();
         Optional<Account> optionalAccount = accountDAO.findByUsername(username);
         if (optionalAccount.isPresent()) {
             model.addAttribute("message", "error");
             model.addAttribute("text", "Username already exists!");
         }else { accountDAO.save(account);
             model.addAttribute("message", "success");
             model.addAttribute("text", "Thêm mới thành công!");}

     }else {
         model.addAttribute("message", "error");
         model.addAttribute("text", "Mật khẩu không khớp !!");

     }


        return "layout_user/signup";
    }
    @RequestMapping("/user/profile")
    public String registerUser(@ModelAttribute("user") User user, Model model, @RequestParam("avatarFile") MultipartFile photo) {
        String username = sessionService.get("username"); // Lấy tên đăng nhập từ session

        Optional<Account> accountOptional = accountDAO.findByUsername(username);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();

            // Kiểm tra xem tài khoản đã có thông tin User hay chưa
            Optional<User> existingUserOptional = userDAO.findByAccount(account);
            if (existingUserOptional.isPresent()) {
                User userToUpdate = existingUserOptional.get();

                // Cập nhật thông tin User từ form
                userToUpdate.setFirstName(user.getFirstName());
                userToUpdate.setLastName(user.getLastName());
                userToUpdate.setBio(user.getBio());
                userToUpdate.setUpdatedAt(LocalDateTime.now()); // Cập nhật thời gian cập nhật

                if (!photo.isEmpty()) {
                    try {
                        // Lưu ảnh đại diện vào thư mục static/images với tên duy nhất
                        String fileName = UUID.randomUUID().toString() + "-" + photo.getOriginalFilename();
                        Path imagesDir = Paths.get("").toAbsolutePath().resolve("src/main/resources/static/images");
                        if (!Files.exists(imagesDir)) {
                            Files.createDirectories(imagesDir);
                        }
                        Files.copy(photo.getInputStream(), imagesDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

                        // Cập nhật URL ảnh đại diện
                        userToUpdate.setAvatarUrl("/images/" + fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                        model.addAttribute("message", "error");
                        model.addAttribute("text", "Lỗi khi lưu ảnh");
                        return "user_manager/profiles";
                    }
                }

                // Lưu thông tin User cập nhật vào CSDL
                userDAO.save(userToUpdate);
                model.addAttribute("user", userToUpdate);
                model.addAttribute("message", "success");
                model.addAttribute("text", "Cập nhật dữ liệu thành công !!");
                model.addAttribute("avatarUrl", user.getAvatarUrl());
            } else {
                // Nếu chưa có thông tin User, tạo mới thông tin User
                user.setAccount(account); // Kết nối User với Account đã có
                if (!photo.isEmpty()) {
                    try {
                        // Lưu ảnh đại diện vào thư mục static/images với tên duy nhất
                        String fileName = UUID.randomUUID().toString() + "-" + photo.getOriginalFilename();
                        Path imagesDir = Paths.get("").toAbsolutePath().resolve("src/main/resources/static/images");
                        if (!Files.exists(imagesDir)) {
                            Files.createDirectories(imagesDir);
                        }
                        Files.copy(photo.getInputStream(), imagesDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

                        // Cập nhật URL ảnh đại diện
                        user.setAvatarUrl("/images/" + fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                        model.addAttribute("message", "error");
                        model.addAttribute("text", "Lỗi khi lưu ảnh");
                        return "user_manager/profiles";
                    }
                }

                // Lưu thông tin User mới vào CSDL
                userDAO.save(user);
                model.addAttribute("user", user);
                model.addAttribute("message", "success");
                model.addAttribute("text", "Dữ liệu đã được thêm!!");
            }

            model.addAttribute("account", account);
            return "user_manager/profiles";
        } else {
            // Xử lý khi không tìm thấy tài khoản (nếu cần thiết)
            model.addAttribute("message", "error");
            model.addAttribute("text", "Vui lòng đăng nhập !!");
            return "layout_user/login"; // Chuyển hướng về trang đăng nhập
        }
    }






}

