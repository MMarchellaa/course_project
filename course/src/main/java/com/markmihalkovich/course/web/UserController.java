package com.markmihalkovich.course.web;

import com.markmihalkovich.course.dto.UserDTO;
import com.markmihalkovich.course.entity.User;
import com.markmihalkovich.course.entity.enums.ERole;
import com.markmihalkovich.course.facade.UserFacade;
import com.markmihalkovich.course.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserFacade userFacade;

    @GetMapping("/")
    public ResponseEntity<UserDTO> getCurrentUser(Principal principal) {
        User user = userService.getCurrentUser(principal);
        UserDTO userDTO = userFacade.userToUserDTO(user);
        System.out.println(user.getRoles());

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable("userId") String userId) {
        User user = userService.getUserById(Long.parseLong(userId));
        UserDTO userDTO = userFacade.userToUserDTO(user);

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<User> users = userService.getAllUsers();
        List<UserDTO> usersDTO = users.stream().map(user -> userFacade.userToUserDTO(user)).collect(Collectors.toList());
        return new ResponseEntity<>(usersDTO, HttpStatus.OK);
    }

    @GetMapping("/{userId}/role")
    public ResponseEntity<String> getUserRole(@PathVariable("userId") String id){
        User user = userService.getUserById(Long.parseLong(id));
        String role = (user.getRoles().contains(ERole.ROLE_USER)) ? "USER" : "ADMIN";
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @PostMapping("/{userId}/unactivate")
    public ResponseEntity<Object> unactivateUser(@PathVariable("userId") String id, Principal principal){
        userService.unactivateUser(Long.parseLong(id), principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{userId}/activate")
    public ResponseEntity<Object> activateUser(@PathVariable("userId") String id, Principal principal){
        userService.activateUser(Long.parseLong(id), principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
