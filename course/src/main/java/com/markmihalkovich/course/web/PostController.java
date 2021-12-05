package com.markmihalkovich.course.web;

import com.markmihalkovich.course.dto.PostDTO;
import com.markmihalkovich.course.facade.PostFacade;
import com.markmihalkovich.course.payload.reponse.MessageResponse;
import com.markmihalkovich.course.services.PostService;
import com.markmihalkovich.course.validations.ResponseErrorValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/post")
@CrossOrigin
public class PostController {

    @Autowired
    private PostFacade postFacade;
    @Autowired
    private PostService postService;
    @Autowired
    private ResponseErrorValidation responseErrorValidation;

    @PostMapping("/create")
    public ResponseEntity<Object> createPost(@Valid @RequestBody PostDTO postDTO,
                                             BindingResult bindingResult,
                                             Principal principal) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        PostDTO createdPost = postFacade.postToPostDTO(postService.createPost(postDTO, principal));

        return new ResponseEntity<>(createdPost, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        List<PostDTO> postDTOList = postService.getAllPosts()
                .stream()
                .map(postFacade::postToPostDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(postDTOList, HttpStatus.OK);
    }

    @GetMapping("/user/posts/{userId}")
    public ResponseEntity<List<PostDTO>> getAllPostsForUser(@PathVariable("userId") Long userId) {
        List<PostDTO> postDTOList = postService.getAllPostForUser(userId)
                .stream()
                .map(postFacade::postToPostDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(postDTOList, HttpStatus.OK);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPost(@PathVariable("postId") Long postId) {
        PostDTO postDTO = postFacade.postToPostDTO(postService.getPostById(postId));
        return new ResponseEntity<>(postDTO, HttpStatus.OK);
    }

    @PostMapping("/user/posts/update")
    public ResponseEntity<Object> updatePost(@Valid @RequestBody PostDTO postDTO, BindingResult bindingResult, Principal principal) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        PostDTO postUpdated = postFacade.postToPostDTO(postService.updatePost(postDTO, principal));
        return new ResponseEntity<>(postUpdated, HttpStatus.OK);
    }

    @PostMapping("/{postId}/{username}/like")
    public ResponseEntity<PostDTO> likePost(@PathVariable("postId") Long postId,
                                            @PathVariable("username") String username) {
        PostDTO postDTO = postFacade.postToPostDTO(postService.likePost(postId, username));

        return new ResponseEntity<>(postDTO, HttpStatus.OK);
    }

    @PostMapping("/{postId}/{username}/rating/{rating}")
    public ResponseEntity<PostDTO> ratePost(@PathVariable("postId") Long postId,
                                            @PathVariable("username") String username,
                                            @PathVariable("rating") Integer rating) {
        PostDTO postDTO = postFacade.postToPostDTO(postService.ratePost(postId, username, rating));

        return new ResponseEntity<>(postDTO, HttpStatus.OK);
    }

    @PostMapping("/{postId}/delete")
    public ResponseEntity<MessageResponse> deletePost(@PathVariable("postId") Long postId, Principal principal) throws Exception {
        postService.deletePost(postId, principal);
        return new ResponseEntity<>(new MessageResponse("Post was deleted"), HttpStatus.OK);
    }

    @GetMapping("/search/{text}")
    public ResponseEntity<List<PostDTO>> searchAll(@PathVariable("text") String text){
        List<PostDTO> postDTOList = postService.searchPosts(text)
                .stream()
                .map(postFacade::postToPostDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(postDTOList, HttpStatus.OK);
    }
}
