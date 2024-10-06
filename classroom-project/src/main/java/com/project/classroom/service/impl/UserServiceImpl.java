package com.project.classroom.service.impl;

import com.project.classroom.dto.request.SearchReqDto;
import com.project.classroom.dto.response.SearchResDto;
import com.project.classroom.entity.User;
import com.project.classroom.repository.UserRepository;
import com.project.classroom.repository.base.SearchRepository;
import com.project.classroom.service.UserService;
import com.project.classroom.service.base.BaseServerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl extends BaseServerImpl<User, UserRepository> implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SearchRepository<User> searchRepository;

    @Override
    public boolean existByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(new User());
    }

    @Override
    public boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public SearchResDto search(SearchReqDto request) {
        SearchResDto response = new SearchResDto();
        response.setLimit(request.getLimit());
        response.setOffset(request.getOffset());
        response.setTotal(userRepository.count());
        response.setData(searchRepository.searchByFields(request, User.class));
        return response;
    }
}
