package com.markmihalkovich.course.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.markmihalkovich.course.entity.Post;
import com.markmihalkovich.course.entity.User;
import com.markmihalkovich.course.repository.PostRepository;
import com.markmihalkovich.course.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class ImageUploadService {
    public static final Logger LOG = LoggerFactory.getLogger(ImageUploadService.class);

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dmxhppjo7",
            "api_key", "874533448789599",
            "api_secret", "DDvCPuJRTqwfLI8Vm0I_LLz0-k0"));

    @Autowired
    public ImageUploadService(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public void uploadImageToPost(MultipartFile file, Principal principal, Long postId) throws IOException {
        User user = getUserByPrincipal(principal);
        Post post = user.getPosts()
                .stream()
                .filter(p -> p.getId().equals(postId))
                .collect(toSinglePostCollector());
        Map<String, String> options = new HashMap<>();
        options.put("tags", postId.toString());
        LOG.info("Uploading image to Post {}", post.getId());
        Set<String> temp = post.getLinkToImages();
        temp.add((String) cloudinary.uploader().upload(file.getBytes(), options).get("url"));
        post.setLinkToImages(temp);
        postRepository.save(post);
    }

    public Set<String> getImageToPost(Long postId) {
        return postRepository.getById(postId).getLinkToImages();
    }

    private User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with username " + username));

    }

    private <T> Collector<T, ?, T> toSinglePostCollector() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException();
                    }
                    return list.get(0);
                }
        );
    }
}
