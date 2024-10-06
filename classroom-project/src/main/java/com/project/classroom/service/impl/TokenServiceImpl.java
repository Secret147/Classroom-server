package com.project.classroom.service.impl;

import com.project.classroom.dto.request.SearchReqDto;
import com.project.classroom.dto.response.SearchResDto;
import com.project.classroom.entity.Token;
import com.project.classroom.repository.TokenRepository;
import com.project.classroom.service.TokenService;
import com.project.classroom.service.base.BaseServerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl extends BaseServerImpl<Token, TokenRepository> implements TokenService {
    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public void update(Token token) {
        tokenRepository.save(token);
    }

    @Override
    public Token findByRefreshToken(String refreshToken) {
        return tokenRepository.findByRefreshToken(refreshToken);
    }

    @Override
    public SearchResDto search(SearchReqDto request) {
        return null;
    }
}
