package com.project.classroom.api.admin;

import com.project.classroom.dto.request.SearchReqDto;
import com.project.classroom.dto.response.SearchResDto;
import com.project.classroom.entity.User;
import com.project.classroom.repository.UserRepository;
import com.project.classroom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("api/admin/user")
public class UserResource {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserService userService;

    @PostMapping
    public SearchResDto findUser (@RequestBody SearchReqDto request) {
        return userService.search(request);
    }
}
