package com.markmihalkovich.course.dto;

import com.markmihalkovich.course.entity.enums.ECategory;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Map;
import java.util.Set;

@Data
public class PostDTO {

    private Long id;
    @NotEmpty
    private String title;
    @NotEmpty
    private String caption;
    private String username;
    private Integer likes;
    private Set<String> usersLiked;
    private Map<String, Integer> ratings;
    private Set<String> linkToImages;
    private ECategory category;
    private Long userId;
}
