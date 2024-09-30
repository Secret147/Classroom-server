package com.project.classroom.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignInReqDto {

        @NotBlank(message = "Email is required!")
        private String email;

        @NotBlank(message = "Password is required!")
        private String password;
}
