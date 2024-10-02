package com.project.classroom.service.impl;

import com.project.classroom.constants.StatusConst;
import com.project.classroom.dto.request.SignInReqDto;
import com.project.classroom.dto.request.SignUpReqDto;
import com.project.classroom.dto.response.BaseResponse;
import com.project.classroom.dto.response.SignInResDto;
import com.project.classroom.entity.Role;
import com.project.classroom.entity.User;
import com.project.classroom.model.RoleFactory;
import com.project.classroom.security.jwt.JwtUtils;
import com.project.classroom.security.userdetail.UserDetailImpl;
import com.project.classroom.service.AuthService;
import com.project.classroom.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.management.relation.RoleNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private RoleFactory roleFactory;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public ResponseEntity<BaseResponse> signUp(SignUpReqDto signUpReqDto)
            throws RoleNotFoundException {
        String email = signUpReqDto.getEmail();
        if (userService.existByEmail(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    BaseResponse.builder()
                            .status(StatusConst.NOT_GOOD)
                            .error("Email already exists")
                            .build()
            );
        }

        if (email == null || !EmailValidator.getInstance()
                .isValid(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    BaseResponse.builder()
                            .status(StatusConst.NOT_GOOD)
                            .error("Email format is incorrect")
                            .build()
            );

        }

        if (userService.existByUsername(signUpReqDto.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    BaseResponse.builder()
                            .status(StatusConst.NOT_GOOD)
                            .error("Provided username already exists. Try sign in or provide another username.")
                            .build()
            );
        }

        User user = createUser(signUpReqDto);
        userService.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponse.builder()
                        .status(StatusConst.OK)
                        .message("User account has been successfully created!")
                        .build()
        );
    }

    @Override
    public ResponseEntity<BaseResponse> signIn(SignInReqDto signInReqDto) {

        String email = signInReqDto.getEmail();
        String password = signInReqDto.getPassword();

        if (email == null || !EmailValidator.getInstance()
                .isValid(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    BaseResponse.builder()
                            .status(StatusConst.NOT_GOOD)
                            .error("Email format is incorrect")
                            .build()
            );

        }

        if (!userService.existByEmail(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    BaseResponse.builder()
                            .status(StatusConst.NOT_GOOD)
                            .error("Email does not exists")
                            .build()
            );
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailImpl userDetails = (UserDetailImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        SignInResDto signInResponseDto = SignInResDto.builder()
                .username(userDetails.getUsername())
                .email(email)
                .id(userDetails.getId())
                .token(jwt)
                .type("Bearer")
                .roles(roles)
                .build();

        return ResponseEntity.ok(
                BaseResponse.builder()
                        .status(StatusConst.OK)
                        .message("Sign in successfull!")
                        .data(signInResponseDto)
                        .build()
        );
    }

    private User createUser(SignUpReqDto signUpReqDto) throws RoleNotFoundException {
        return User.builder()
                .username(signUpReqDto.getUsername())
                .email(signUpReqDto.getEmail())
                .password(passwordEncoder.encode(signUpReqDto.getPassword()))
                .roles(determineRoles(signUpReqDto.getRoles()))
                .address(signUpReqDto.getAddress())
                .uid(RandomStringUtils.randomAlphabetic(20))
                .enabled(true)
                .build();
    }

    private Set<Role> determineRoles(Set<String> strRoles) throws RoleNotFoundException {
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            roles.add(roleFactory.getInstance("user"));
        } else {
            for (String role : strRoles) {
                roles.add(roleFactory.getInstance(role));
            }
        }
        return roles;
    }
}
