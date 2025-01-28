package com.tokioschool.storeapp.service;


import com.tokioschool.storeapp.dto.authentication.AuthenticatedMeResponseDTO;
import com.tokioschool.storeapp.dto.authentication.AuthenticationRequestDTO;
import com.tokioschool.storeapp.dto.authentication.AuthenticationResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;

import javax.security.auth.login.LoginException;

public interface AuthenticationService {

    AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationResponseDTO) throws BadCredentialsException;

    @PreAuthorize("hasRole('ADMIN')")
    AuthenticatedMeResponseDTO getAuthenticated() throws LoginException;

    @PreAuthorize("isAuthenticated()")
    Pair<String, Long> getTokenAndExpiredAt(HttpServletRequest request);
}
