package com.example.bongtube.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Entity
@Table(name = "video")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id")
    private Long videoId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title")
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "url")
    private String url;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl; // Thay đổi từ MultipartFile thành String

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "views_count", columnDefinition = "bigint default 0")
    private Long viewsCount = 0L; // Đặt giá trị mặc định

    @Column(name = "likes_count", columnDefinition = "bigint default 0")
    private Long likesCount;

    @Column(name = "dislikes_count", columnDefinition = "bigint default 0")
    private Long dislikesCount;


    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "status")
    private String status;
    @Transient
    private MultipartFile thumbnailFile; // Giữ lại để xử lý tải lên file

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Sử dụng @PreUpdate để tự động cập nhật giá trị cho updatedAt trước khi update entity
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    public Video(Long videoId) {
        this.videoId = videoId;
    }
}