package com.project.classroom.service;

import com.project.classroom.entity.Token;
import com.project.classroom.service.base.BaseService;

public interface TokenService extends BaseService<Token> {

    void update(Token token);

    Token findByRefreshToken(String refreshToken);
}
