package com.markmihalkovich.course.services;

import com.markmihalkovich.course.entity.User;
import com.markmihalkovich.course.entity.enums.ERole;
import com.markmihalkovich.course.exceptions.UserExistException;
import com.markmihalkovich.course.payload.request.SignupRequest;
import com.markmihalkovich.course.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;

@Service
@Transactional
public class UserService {
    public static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(SignupRequest userIn) {
        User user = new User();
        user.setEmail(userIn.getEmail());
        user.setUsername(userIn.getUsername());
        user.setPassword(passwordEncoder.encode(userIn.getPassword()));
        user.getRoles().add(ERole.ROLE_USER);
        user.setIsActive("ACTIVE");

        try {
            LOG.info("Saving User {}", userIn.getEmail());
            userRepository.save(user);
        } catch (Exception e) {
            LOG.error("Error during registration. {}", e.getMessage());
            throw new UserExistException("The user " + user.getUsername() + " already exist. Please check credentials");
        }
    }

    public User getCurrentUser(Principal principal) {
        return getUserByPrincipal(principal);
    }

    private User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with username " + username));

    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public List<User> getAllUsers(){
        return userRepository.findUsersByRoles(ERole.ROLE_USER).orElseThrow();
    }

    public void unactivateUser(Long userId, Principal principal){
        User user = getUserByPrincipal(principal);
        if (user.getRoles().contains(ERole.ROLE_ADMIN)){
            User user1 = getUserById(userId);
            user1.setIsActive("UNACTIVE");
        }
    }

    public void activateUser(Long userId, Principal principal){
        User user = getUserByPrincipal(principal);
        if (user.getRoles().contains(ERole.ROLE_ADMIN)){
            User user1 = getUserById(userId);
            user1.setIsActive("ACTIVE");
        }
    }
}
