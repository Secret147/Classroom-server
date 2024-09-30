package com.project.classroom.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SignInResDto {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String username;
    private List<String> roles;
}

