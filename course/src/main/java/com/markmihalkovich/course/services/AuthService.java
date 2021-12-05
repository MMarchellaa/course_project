package com.markmihalkovich.course.services;

import com.markmihalkovich.course.credentials.CredentialsProperties;
import com.markmihalkovich.course.payload.request.LoginRequest;
import com.markmihalkovich.course.security.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTTokenProvider jwtTokenProvider;
    @Autowired
    private CredentialsProperties credentialsProperties;

    public String jwtGenerator(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return credentialsProperties.getSecurityConstants().getTokenPrefix() + jwtTokenProvider.generateToken(authentication);
    }
}
