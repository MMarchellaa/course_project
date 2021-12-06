package com.markmihalkovich.course.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.markmihalkovich.course.credentials.CredentialsProperties;
import com.markmihalkovich.course.entity.Post;
import com.markmihalkovich.course.entity.User;
import com.markmihalkovich.course.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@EnableConfigurationProperties(CredentialsProperties.class)
public class ImageUploadService {
    public static final Logger LOG = LoggerFactory.getLogger(ImageUploadService.class);

    private final PostRepository postRepository;
    private final Cloudinary cloudinary;
    private final UserService userService;

    @Autowired
    public ImageUploadService(PostRepository postRepository, CredentialsProperties credentialsProperties, UserService userService) {
        this.postRepository = postRepository;
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", credentialsProperties.getCloudinaryCredentials().getCloudName(),
                "api_key", credentialsProperties.getCloudinaryCredentials().getApiKey(),
                "api_secret", credentialsProperties.getCloudinaryCredentials().getApiSecret()));
        this.userService = userService;
    }

    @Transactional
    public void uploadImageToPost(MultipartFile file, Principal principal, Long postId) throws IOException {
        User user = this.userService.getUserByPrincipal(principal);
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
