package com.markmihalkovich.course.web;

import com.markmihalkovich.course.dto.UserDTO;
import com.markmihalkovich.course.entity.enums.ERole;
import com.markmihalkovich.course.facade.UserFacade;
import com.markmihalkovich.course.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
        UserDTO userDTO = userFacade.userToUserDTO(userService.getCurrentUser(principal));
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable("userId") String userId) {
        UserDTO userDTO = userFacade.userToUserDTO(userService.getUserById(Long.parseLong(userId)));
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<UserDTO> usersDTO = userService.getAllUsers().stream().map(user -> userFacade.userToUserDTO(user)).collect(Collectors.toList());
        return new ResponseEntity<>(usersDTO, HttpStatus.OK);
    }

    @GetMapping("/{userId}/role")
    public ResponseEntity<String> getUserRole(@PathVariable("userId") String id){
        String role = (userService.getUserById(Long.parseLong(id)).getRoles().contains(ERole.ROLE_USER)) ? "USER" : "ADMIN";
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @PostMapping("/unactivate")
    public ResponseEntity<Object> unactivateUser(@Valid @RequestBody String id, Principal principal){
        userService.unactivateUser(Long.parseLong(id), principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/activate")
    public ResponseEntity<Object> activateUser(@Valid @RequestBody String id, Principal principal){
        userService.activateUser(Long.parseLong(id), principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/isenable/{email}")
    public ResponseEntity<String> isEnabled(@PathVariable("email") String email){
        String isEnabled = userService.isEnabled(email);
        return new ResponseEntity<>(isEnabled, HttpStatus.OK);
    }
}
