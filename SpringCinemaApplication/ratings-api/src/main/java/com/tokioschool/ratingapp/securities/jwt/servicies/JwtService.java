package com.tokioschool.ratingapp.securities.jwt.servicies;

import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    SignedJWT generateToken(UserDetails userDetails);

}