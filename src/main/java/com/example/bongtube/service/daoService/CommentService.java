package com.example.bongtube.service.daoService;

import com.example.bongtube.dao.CommentDAO;
import com.example.bongtube.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {


    @Autowired
    private CommentDAO commentRepository;

    public List<Comment> getCommentsByVideoId(Long videoId) {
        return commentRepository.findByVideo_VideoId(videoId); // Sử dụng phương thức đúng theo repository
    }
    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(Math.toIntExact(commentId));
    }

    public Optional<Comment> getCommentById(Long commentId) {
        return commentRepository.findById(Math.toIntExact(commentId));
    }
}