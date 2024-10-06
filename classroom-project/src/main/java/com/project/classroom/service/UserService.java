package com.project.classroom.service;

import com.project.classroom.entity.User;
import com.project.classroom.service.base.BaseService;

public interface UserService extends BaseService<User> {
    boolean existByEmail(String email);

    boolean existByUsername(String username);

    User findByEmail(String email);
}
