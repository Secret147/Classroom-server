package com.project.classroom.service;

import com.project.classroom.entity.User;

public interface UserService {
    boolean existByEmail(String email);
    void save(User user);

    boolean existByUsername(String username);
}
