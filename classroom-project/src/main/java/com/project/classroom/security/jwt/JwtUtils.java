package com.project.classroom.security.jwt;

import com.project.classroom.constants.KeyConst;
import com.project.classroom.security.userdetail.UserDetailImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Autowired
    private Environment environment;
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    public String generateJwtToken(Authentication authentication) {
        UserDetailImpl userPrincipal = (UserDetailImpl) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject((userPrincipal.getEmail()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + KeyConst.JWT_EXPIRATION))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateJwtTokenByEmail(String email) {
        return Jwts.builder()
                .setSubject((email))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + KeyConst.JWT_EXPIRATION))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshJwtTokenByEmail(String email) {
        return Jwts.builder()
                .setSubject((email))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + KeyConst.REFRESH_JWT_EXPIRATION))
                .signWith(refreshTokenKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshJwtToken(Authentication authentication) {
        UserDetailImpl userPrincipal = (UserDetailImpl) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject((userPrincipal.getEmail()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + KeyConst.REFRESH_JWT_EXPIRATION))
                .signWith(refreshTokenKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(KeyConst.JWT_SECRET.getBytes());
    }

    private Key refreshTokenKey() {
        return Keys.hmacShaKeyFor(KeyConst.REFRESH_JWT_SECRET.getBytes());
    }


    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String getUserNameFromRefreshJwtToken(String token) {
        return Jwts.parser().setSigningKey(refreshTokenKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (SignatureException e) {
            logger.error("JWT signature does not match locally computed signature: {}", e.getMessage());
        }

        return false;
    }

    public String validateRefreshJwtToken(String token) {
        try {
            Jwts.parser().setSigningKey(refreshTokenKey()).build().parse(token);
            return "1";
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            return "Invalid JWT refresh token";
        } catch (ExpiredJwtException e) {
            logger.error("JWT refresh token is expired: {}", e.getMessage());
            return "JWT refresh token is expired";
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
            return "JWT refresh token is unsupported";
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
            return "JWT claims string is empty";
        } catch (SignatureException e) {
            logger.error("JWT signature does not match locally computed signature: {}", e.getMessage());
            return "JWT signature does not match locally computed signature";
        }

    }
}
