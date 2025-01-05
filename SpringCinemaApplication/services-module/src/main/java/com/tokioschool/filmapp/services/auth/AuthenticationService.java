package com.tokioschool.filmapp.services.auth;

import com.tokioschool.filmapp.dto.auth.AuthenticatedMeResponseDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationRequestDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.tuple.Pair;


public interface AuthenticationService {
    AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationResponseDTO);
    AuthenticatedMeResponseDTO getAuthenticated();
    Pair<String, Long> getTokenAndExpiredAt(HttpServletRequest request);
}
