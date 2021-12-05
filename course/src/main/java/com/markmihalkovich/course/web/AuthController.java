package com.markmihalkovich.course.web;

import com.markmihalkovich.course.payload.reponse.JWTTokenSuccessResponse;
import com.markmihalkovich.course.payload.reponse.MessageResponse;
import com.markmihalkovich.course.payload.request.LoginRequest;
import com.markmihalkovich.course.payload.request.SignupRequest;
import com.markmihalkovich.course.services.AuthService;
import com.markmihalkovich.course.services.UserService;
import com.markmihalkovich.course.validations.ResponseErrorValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
@PreAuthorize("permitAll()")
public class AuthController {

    @Autowired
    private ResponseErrorValidation responseErrorValidation;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        String jwt = authService.jwtGenerator(loginRequest);

        return ResponseEntity.ok(new JWTTokenSuccessResponse(true, jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignupRequest signupRequest, BindingResult bindingResult) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        userService.createUser(signupRequest);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}