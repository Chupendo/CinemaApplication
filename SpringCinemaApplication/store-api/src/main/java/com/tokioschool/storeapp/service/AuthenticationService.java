package com.tokioschool.storeapp.service;


import com.tokioschool.storeapp.dto.authentication.AuthenticatedMeResponseDTO;
import com.tokioschool.storeapp.dto.authentication.AuthenticationRequestDTO;
import com.tokioschool.storeapp.dto.authentication.AuthenticationResponseDTO;
import org.springframework.security.authentication.BadCredentialsException;

import javax.security.auth.login.LoginException;

public interface AuthenticationService {
    AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationResponseDTO) throws BadCredentialsException;
    AuthenticatedMeResponseDTO getAuthenticated() throws LoginException;
}
