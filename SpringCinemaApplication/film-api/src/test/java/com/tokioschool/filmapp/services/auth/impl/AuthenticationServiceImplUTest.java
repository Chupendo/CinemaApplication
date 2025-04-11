package com.tokioschool.filmapp.services.auth.impl;

import com.tokioschool.filmapp.dto.auth.AuthenticatedMeResponseDto;
import com.tokioschool.filmapp.services.auth.impl.AuthenticationServiceImpl;
import com.tokioschool.filmapp.services.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
class AuthenticationServiceImplUTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationService = new AuthenticationServiceImpl(authenticationManager, jwtService);
    }

    @Test
    void getAuthenticated_withAuthorities_shouldReturnCorrectRolesAndScopes() {
        Authentication authentication = mock(Authentication.class);
        final List<GrantedAuthority> authoritesMock = List.of(
                (GrantedAuthority) () -> "ROLE_ADMIN",
                (GrantedAuthority) () -> "SCOPE_WRITE",
                (GrantedAuthority) () -> "CUSTOM_PERMISSION"
        );

        when(authentication.getName()).thenReturn("username");
        when(authentication.getAuthorities()).thenReturn( (Collection) authoritesMock );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AuthenticatedMeResponseDto response = authenticationService.getAuthenticated();

        assertNotNull(response);
        assertEquals("username", response.getUsername());
        assertTrue(response.getRoles().contains("ROLE_ADMIN"));
        assertTrue(response.getScopes().contains("SCOPE_WRITE"));
        assertTrue(response.getAuthorities().contains("CUSTOM_PERMISSION"));
    }

    @Test
    void getTokenAndExpiredAt_withExpiredToken_shouldReturnNull() {
        Authentication authentication = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer expiredToken");
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getTokenValue()).thenReturn("expiredToken");
        when(jwt.getExpiresAt()).thenReturn(Instant.now().minus(1, ChronoUnit.HOURS));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Pair<String, Long> result = authenticationService.getTokenAndExpiredAt(request);

        assertNull(result);
    }

    @Test
    void getTokenAndExpiredAt_withNullAuthentication_shouldReturnNull() {
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        SecurityContextHolder.getContext().setAuthentication(null);

        Pair<String, Long> result = authenticationService.getTokenAndExpiredAt(request);

        assertNull(result);
    }
}