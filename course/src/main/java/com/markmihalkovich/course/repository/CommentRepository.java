package com.markmihalkovich.course.repository;

import com.markmihalkovich.course.entity.Comment;
import com.markmihalkovich.course.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPost(Post post);

    Comment findByIdAndUserId(Long commentId, Long userId);

    List<Comment> getCommentsByMessage(String message);

}
