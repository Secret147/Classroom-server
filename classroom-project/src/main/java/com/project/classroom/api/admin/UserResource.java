package com.project.classroom.api.admin;

import com.project.classroom.entity.User;
import com.project.classroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("admin/user")
public class UserResource {

    @Autowired
    private UserRepository userRepo;

    @GetMapping()
    public Optional<User> findUser () {

        System.out.println("test");
        return userRepo.findByEmail("d");
    }
}
