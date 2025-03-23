package com.tokioschool.ratingapp.services;

import com.tokioschool.filmapp.dto.auth.AuthenticatedMeResponseDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationRequestDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.security.auth.login.LoginException;
import java.nio.file.AccessDeniedException;

public interface AuthenticationService {

    AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationResponseDTO) throws Exception;

    AuthenticatedMeResponseDTO getAuthenticated() throws LoginException;

    Pair<String, Long> getTokenAndExpiredAt(HttpServletRequest request);

}
