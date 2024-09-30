package com.project.classroom.service;

import com.project.classroom.dto.request.SignInReqDto;
import com.project.classroom.dto.request.SignUpReqDto;
import com.project.classroom.dto.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;

public interface AuthService {
    ResponseEntity<BaseResponse> signUp(SignUpReqDto signUpReqDto) throws RoleNotFoundException;

    ResponseEntity<BaseResponse> signIn(SignInReqDto signUpReqDto);
}

