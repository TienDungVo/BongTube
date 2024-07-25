package com.example.bongtube.dao;

import com.example.bongtube.entity.LikeDislike;
import com.example.bongtube.entity.User;
import com.example.bongtube.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeDislikeDAO extends JpaRepository<LikeDislike, Long> {
    Optional<LikeDislike> findByUserAndVideoAndType(User user, Video video, String type);
}
