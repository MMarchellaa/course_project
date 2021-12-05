package com.markmihalkovich.course.web;

import com.markmihalkovich.course.dto.CommentDTO;
import com.markmihalkovich.course.facade.CommentFacade;
import com.markmihalkovich.course.payload.reponse.MessageResponse;
import com.markmihalkovich.course.services.CommentService;
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
@RequestMapping("api/comment")
@CrossOrigin
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentFacade commentFacade;
    @Autowired
    private ResponseErrorValidation responseErrorValidation;

    @PostMapping("/{postId}/create")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDTO commentDTO,
                                                @PathVariable("postId") Long postId,
                                                BindingResult bindingResult,
                                                Principal principal) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        CommentDTO createdComment = commentFacade.commentToCommentDTO(commentService.saveComment(postId, commentDTO, principal));

        return new ResponseEntity<>(createdComment, HttpStatus.OK);
    }

    @GetMapping("/all/{postId}")
    public ResponseEntity<List<CommentDTO>> getAllCommentsToPost(@PathVariable("postId") Long postId) {
        List<CommentDTO> commentDTOList = commentService.getAllCommentsForPost(postId)
                .stream()
                .map(commentFacade::commentToCommentDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(commentDTOList, HttpStatus.OK);
    }

    @PostMapping("/{commentId}/delete")
    public ResponseEntity<MessageResponse> deleteComment(@PathVariable("commentId") Long commentId, Principal principal) {
        commentService.deleteComment(commentId, principal);
        return new ResponseEntity<>(new MessageResponse("Post was deleted"), HttpStatus.OK);
    }

}
