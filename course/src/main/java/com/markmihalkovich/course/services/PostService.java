package com.markmihalkovich.course.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.markmihalkovich.course.credentials.CredentialsProperties;
import com.markmihalkovich.course.dto.PostDTO;
import com.markmihalkovich.course.entity.Post;
import com.markmihalkovich.course.entity.User;
import com.markmihalkovich.course.entity.enums.ERole;
import com.markmihalkovich.course.exceptions.PostNotFoundException;
import com.markmihalkovich.course.repository.PostRepository;
import com.markmihalkovich.course.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.*;

@Service
public class PostService {
    public static final Logger LOG = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final Cloudinary cloudinary;
    private final UserService userService;


    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, CredentialsProperties credentialsProperties, UserService userService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", credentialsProperties.getCloudinaryCredentials().getCloudName(),
                "api_key", credentialsProperties.getCloudinaryCredentials().getApiKey(),
                "api_secret", credentialsProperties.getCloudinaryCredentials().getApiSecret()));
        this.userService = userService;
    }

    @Transactional
    public Post createPost(PostDTO postDTO, Principal principal) {
        User user = this.userService.getUserByPrincipal(principal);
        Post post = new Post();
        post.setUser(user);
        post.setCaption(postDTO.getCaption());
        post.setTitle(postDTO.getTitle());
        post.setLikes(0);
        post.setLinkToImages(postDTO.getLinkToImages());
        post.setCategory(postDTO.getCategory());

        LOG.info("Saving Post for User: {}", user.getEmail());
        return postRepository.save(post);
    }

    @Transactional
    public Post updatePost(PostDTO postDTO, Principal principal) {
        User user = this.userService.getUserByPrincipal(principal);
        if (!postDTO.getUserId().equals(user.getId()) && !user.getRoles().contains(ERole.ROLE_ADMIN)){
            throw new UsernameNotFoundException("User not found");
        }
        Post post = getPostById(postDTO.getId());
        post.setCaption(postDTO.getCaption());
        post.setTitle(postDTO.getTitle());
        post.setLinkToImages(postDTO.getLinkToImages());
        post.setCategory(postDTO.getCategory());

        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedDateDesc();
    }

    public Post getPostById(Long postId) {
        return postRepository.findPostById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be found"));
    }

    public List<Post> getAllPostForUser(Long id) {
        User user = userRepository.findUserById(id).orElseThrow(() -> new UsernameNotFoundException("User has not bben found"));
        return postRepository.findAllByUserOrderByCreatedDateDesc(user);
    }

    @Transactional
    public Post likePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be found"));

        Optional<String> userLiked = post.getLikedUsers()
                .stream()
                .filter(u -> u.equals(username)).findAny();

        if (userLiked.isPresent()) {
            post.setLikes(post.getLikes() - 1);
            post.getLikedUsers().remove(username);
        } else {
            post.setLikes(post.getLikes() + 1);
            post.getLikedUsers().add(username);
        }
        return postRepository.save(post);
    }

    @Transactional
    public Post ratePost(Long postId, String username, Integer rating) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be found"));

        boolean userRated = post.getRatings().containsKey(username);

        if (userRated) {
            post.getRatings().replace(username, rating);
        } else {
            post.getRatings().put(username, rating);
        }
        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long postId, Principal principal) throws Exception {
        User user = this.userService.getUserByPrincipal(principal);
        Post post = getPostById(postId);
        if (!post.getUser().getId().equals(user.getId()) && !user.getRoles().contains(ERole.ROLE_ADMIN)){
            throw new UsernameNotFoundException("User not found");
        }
        Map<String, String> options = new HashMap<>();
        try {
            cloudinary.api().deleteResourcesByTag(postId.toString(), options);
        } finally {
            postRepository.delete(post);
        }
    }

    public List<Post> searchPosts(String text) {
        return postRepository.findPostsByTitleLikeOrCaptionLike(text, text);
    }
}
