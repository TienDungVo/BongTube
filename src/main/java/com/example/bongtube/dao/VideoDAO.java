package com.example.bongtube.dao;

import com.example.bongtube.entity.User;
import com.example.bongtube.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoDAO extends JpaRepository<Video, Long> {
    List<Video> findByUser(User user);
    @Query("SELECT v FROM Video v ORDER BY v.viewsCount DESC")
    List<Video> findTop10ByOrderByViewsCountDesc();

}
