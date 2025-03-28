package com.tokioschool.ratingapp.services;

import com.tokioschool.ratingapp.dtos.AuthenticatedMeResponseDTO;
import com.tokioschool.ratingapp.dtos.AuthenticationRequestDTO;
import com.tokioschool.ratingapp.dtos.AuthenticationResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.tuple.Pair;

import javax.security.auth.login.LoginException;

public interface AuthenticationService {
    AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationResponseDTO) throws Exception;

    AuthenticatedMeResponseDTO getAuthenticated() throws LoginException;

    Pair<String, Long> getTokenAndExpiredAt(HttpServletRequest request);
}
