package com.markmihalkovich.course.dto;

import com.markmihalkovich.course.entity.enums.ERole;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserDTO {

    private Long id;
    @NotEmpty
    private String username;
    @NotEmpty
    private String email;
    @NotEmpty
    private ERole role;
    @NotEmpty
    private String isActive;
}
