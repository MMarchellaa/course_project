package com.markmihalkovich.course.facade;

import com.markmihalkovich.course.dto.PostDTO;
import com.markmihalkovich.course.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostFacade {

    public PostDTO postToPostDTO(Post post) {
        PostDTO postDTO = new PostDTO();
        postDTO.setUsername(post.getUser().getUsername());
        postDTO.setId(post.getId());
        postDTO.setCaption(post.getCaption());
        postDTO.setLikes(post.getLikes());
        postDTO.setUsersLiked(post.getLikedUsers());
        postDTO.setTitle(post.getTitle());
        postDTO.setRatings(post.getRatings());
        postDTO.setLinkToImages(post.getLinkToImages());
        postDTO.setCategory(post.getCategory());
        postDTO.setUserId(post.getUser().getId());

        return postDTO;
    }

}
