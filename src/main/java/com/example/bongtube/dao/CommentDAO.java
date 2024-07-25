package com.example.bongtube.dao;

import com.example.bongtube.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentDAO extends JpaRepository<Comment, Integer> {
    List<Comment> findByVideo_VideoId(Long videoId);
    List<Comment> findByParentComment_CommentId(Long parentCommentId);
}
