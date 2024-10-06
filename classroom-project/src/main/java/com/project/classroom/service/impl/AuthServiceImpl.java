package com.project.classroom.service.impl;

import com.project.classroom.constants.KeyConst;
import com.project.classroom.constants.StatusConst;
import com.project.classroom.constants.ValidateConst;
import com.project.classroom.dto.request.SignInReqDto;
import com.project.classroom.dto.request.SignUpReqDto;
import com.project.classroom.dto.response.BaseResponse;
import com.project.classroom.dto.response.SignInResDto;
import com.project.classroom.entity.Role;
import com.project.classroom.entity.Token;
import com.project.classroom.entity.User;
import com.project.classroom.model.RoleFactory;
import com.project.classroom.security.jwt.JwtUtils;
import com.project.classroom.security.userdetail.UserDetailImpl;
import com.project.classroom.service.AuthService;
import com.project.classroom.service.TokenService;
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
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    @Autowired
    private TokenService tokenService;

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

        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    BaseResponse.builder()
                            .status(StatusConst.NOT_GOOD)
                            .error("User does not exists")
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
        String refreshToken = jwtUtils.generateRefreshJwtToken(authentication);
        Date tokenExpired = addMillisecondsToCurrentDate(KeyConst.JWT_EXPIRATION);
        Date refreshTokenExpired = addMillisecondsToCurrentDate(KeyConst.REFRESH_JWT_EXPIRATION);

        UserDetailImpl userDetails = (UserDetailImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        SignInResDto signInResponseDto = SignInResDto.builder()
                .username(userDetails.getUsername())
                .email(email)
                .refreshToken(refreshToken)
                .id(userDetails.getId())
                .token(jwt)
                .type("Bearer")
                .roles(roles)
                .build();

        Token token = Token.builder()
                .user(user)
                .token(jwt)
                .refreshToken(refreshToken)
                .tokenExpiredDate(tokenExpired)
                .refreshTokenExpiredDate(refreshTokenExpired)
                .build();

        tokenService.save(token);


        return ResponseEntity.ok(
                BaseResponse.builder()
                        .status(StatusConst.OK)
                        .message("Sign in successfull!")
                        .data(signInResponseDto)
                        .build()
        );
    }

    @Override
    public ResponseEntity<BaseResponse> refreshToken(String refreshToken) {
        String validateToken = jwtUtils.validateRefreshJwtToken(refreshToken);
        if (!validateToken.equals(ValidateConst.VALIDATE_SUCCESS)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    BaseResponse.builder()
                            .status(StatusConst.NOT_GOOD)
                            .error(validateToken)
                            .build()
            );
        }

        String email = jwtUtils.getUserNameFromRefreshJwtToken(refreshToken);

        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    BaseResponse.builder()
                            .status(StatusConst.NOT_GOOD)
                            .error("User does not exists")
                            .build()
            );
        }

        Token token = tokenService.findByRefreshToken(refreshToken);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    BaseResponse.builder()
                            .status(StatusConst.NOT_GOOD)
                            .error("Token does not exists")
                            .build()
            );
        }

        String jwt = jwtUtils.generateJwtTokenByEmail(email);
        String newRefreshToken = jwtUtils.generateRefreshJwtTokenByEmail(email);
        Date tokenExpired = addMillisecondsToCurrentDate(KeyConst.JWT_EXPIRATION);
        Date refreshTokenExpired = addMillisecondsToCurrentDate(KeyConst.REFRESH_JWT_EXPIRATION);

        Token createToken = Token.builder()
                .token(jwt)
                .user(user)
                .refreshToken(newRefreshToken)
                .tokenExpiredDate(tokenExpired)
                .refreshTokenExpiredDate(refreshTokenExpired)
                .build();
        createToken.setId(token.getId());

        List<String> roles = new ArrayList<>();
        for (Role role : user.getRoles()) {
            roles.add(String.valueOf(role.getName()));
        }

        SignInResDto signInResponseDto = SignInResDto.builder()
                .username(user.getUsername())
                .email(email)
                .refreshToken(newRefreshToken)
                .token(jwt)
                .type("Bearer")
                .roles(roles)
                .build();

        tokenService.update(createToken);

        return ResponseEntity.ok(
                BaseResponse.builder()
                        .status(StatusConst.OK)
                        .message("Refresh token successfull!")
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

    public Date addMillisecondsToCurrentDate(long milliseconds) {

        Date now = new Date();
        long newTimeInMillis = now.getTime() + milliseconds;

        return new Date(newTimeInMillis);
    }
}
