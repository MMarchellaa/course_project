package com.markmihalkovich.course.services;

import com.markmihalkovich.course.dto.CommentDTO;
import com.markmihalkovich.course.entity.Comment;
import com.markmihalkovich.course.entity.Post;
import com.markmihalkovich.course.entity.User;
import com.markmihalkovich.course.entity.enums.ERole;
import com.markmihalkovich.course.exceptions.PostNotFoundException;
import com.markmihalkovich.course.repository.CommentRepository;
import com.markmihalkovich.course.repository.PostRepository;
import com.markmihalkovich.course.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {
    public static final Logger LOG = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Comment saveComment(Long postId, CommentDTO commentDTO, Principal principal) {
        User user = getUserByPrincipal(principal);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be found for username: " + user.getEmail()));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUserId(user.getId());
        comment.setUsername(user.getUsername());
        comment.setMessage(commentDTO.getMessage());

        LOG.info("Saving comment for Post: {}", post.getId());
        return commentRepository.save(comment);
    }

    public List<Comment> getAllCommentsForPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be found"));

        return commentRepository.findAllByPost(post);
    }

    public void deleteComment(Long commentId, Principal principal) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        User user = getUserByPrincipal(principal);
        if (comment.isPresent()) {
            if (!comment.get().getUserId().equals(user.getId()) || !user.getRoles().contains(ERole.ROLE_ADMIN) ||
                    !comment.get().getPost().getUser().getId().equals(user.getId())) {
                throw new UsernameNotFoundException("User not found");
            }
            comment.ifPresent(commentRepository::delete);
        }
    }


    private User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with username " + username));
    }

    public List<Post> getPostsByComment(String text) {
        return List.copyOf(commentRepository.getCommentsByMessage(text).stream().map(Comment::getPost).collect(Collectors.toSet()));
    }
}
