package com.markmihalkovich.course.facade;

import com.markmihalkovich.course.dto.UserDTO;
import com.markmihalkovich.course.entity.User;
import com.markmihalkovich.course.entity.enums.ERole;
import org.springframework.stereotype.Component;

@Component
public class UserFacade {

    public UserDTO userToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole((user.getRoles().contains(ERole.ROLE_USER)) ? ERole.ROLE_USER : ERole.ROLE_ADMIN);
        userDTO.setIsActive(user.getIsActive());

        return userDTO;
    }
}
