package com.tokioschool.ratingapp.services.impl;

import com.tokioschool.ratingapp.dtos.AuthenticatedMeResponseDTO;
import com.tokioschool.ratingapp.dtos.AuthenticationRequestDTO;
import com.tokioschool.ratingapp.dtos.AuthenticationResponseDTO;
import com.tokioschool.ratingapp.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {


    @Override
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationResponseDTO) throws Exception {
        return null;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public AuthenticatedMeResponseDTO getAuthenticated() throws LoginException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null){
            throw new LoginException("don't user authenticated");
        }

        return AuthenticatedMeResponseDTO.builder()
                .username(authentication.getName())
                .scopes(
                        Optional.of(authentication)
                                .map(Authentication::getPrincipal)
                                .filter(Jwt.class::isInstance)
                                .map(jwt -> (Jwt)jwt)
                                .map(token -> token.getClaim("scope"))
                                .filter(List.class::isInstance)
                                .map(scopes ->(List<String>)scopes)
                                .orElseGet(Collections::emptyList)
                )
                .authorities(
                        Optional.of(authentication)
                                .map(Authentication::getAuthorities)
                                .filter(grantedAuthorities -> !grantedAuthorities.isEmpty())
                                .stream()
                                .flatMap(Collection::stream)
                                .map(GrantedAuthority::getAuthority)
                                .filter(value -> !value.toUpperCase().startsWith("ROLE_") && !value.toUpperCase().startsWith("SCOPE_"))
                                .toList()

                )
                .roles(
                        Optional.of(authentication)
                                .map(Authentication::getAuthorities)
                                .filter(grantedAuthorities -> !grantedAuthorities.isEmpty())
                                .stream()
                                .flatMap(Collection::stream)
                                .map(GrantedAuthority::getAuthority)
                                .filter(value -> value.toUpperCase().startsWith("ROLE_"))
                                .toList()
                ).build();
    }

    @Override
    public Pair<String, Long> getTokenAndExpiredAt(HttpServletRequest request) {
        return null;
    }
}

