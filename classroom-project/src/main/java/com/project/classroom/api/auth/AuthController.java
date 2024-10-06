package com.project.classroom.api.auth;

import com.project.classroom.dto.request.RefreshTokenDto;
import com.project.classroom.dto.request.SignInReqDto;
import com.project.classroom.dto.request.SignUpReqDto;
import com.project.classroom.dto.response.BaseResponse;
import com.project.classroom.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.relation.RoleNotFoundException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<BaseResponse> registerUser(@RequestBody @Valid SignUpReqDto signUpReqDto)
            throws RoleNotFoundException {
        return authService.signUp(signUpReqDto);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<BaseResponse> signInUser(@RequestBody @Valid SignInReqDto signInReqDto){
        return authService.signIn(signInReqDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse> refreshToken(@RequestBody RefreshTokenDto refreshTokenDto){
        return authService.refreshToken(refreshTokenDto.getRefreshToken());
    }
}