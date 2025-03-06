package com.tokioschool.ratingapp.securities.jwt.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

import java.nio.file.AccessDeniedException;

public interface JwtService {
    Jwt generateToken(UserDetails userDetails) throws UnsupportedOperationException, AccessDeniedException;
}
