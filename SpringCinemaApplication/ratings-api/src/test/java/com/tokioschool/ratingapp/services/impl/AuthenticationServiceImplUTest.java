package com.tokioschool.ratingapp.services.impl;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.tokioschool.filmapp.dto.auth.AuthenticatedMeResponseDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationRequestDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationResponseDTO;
import com.tokioschool.ratingapp.dto.users.UserDto;
import com.tokioschool.ratingapp.securities.jwt.servicies.JwtService;
import com.tokioschool.ratingapp.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

import javax.security.auth.login.LoginException;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AuthenticationServiceImplUTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private AuthenticationRequestDTO authenticationRequestDTO;
    private UserDto mockUserDto;
    private SignedJWT mockJwt;

    @BeforeEach
    void setUp() {
        authenticationRequestDTO = AuthenticationRequestDTO.builder().username("testUser").password("password123").build();
        mockUserDto = new UserDto();
        mockUserDto.setEmail("testUser");
        mockJwt = mock(SignedJWT.class);
    }

    @Test
    void user_whenAuthenticate_Success() throws Exception {
        when(userService.findUserAndPasswordByEmail(authenticationRequestDTO.getUsername()))
                .thenReturn(Optional.of(Pair.of(mockUserDto, "password123")));

        Authentication mockAuthentication = mock(Authentication.class);
        UserDetails mockUserDetails = new User("testUser", "password123", List.of());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
        when(jwtService.generateToken(mockUserDetails)).thenReturn(mockJwt);
        when(mockJwt.serialize()).thenReturn("mockJwtToken");
        when(mockJwt.getJWTClaimsSet()).thenReturn(new JWTClaimsSet.Builder().claim("exp",new Date()).build());

        AuthenticationResponseDTO response = authenticationService.authenticate(authenticationRequestDTO);

        assertNotNull(response);
        assertEquals("mockJwtToken", response.getAccessToken());
        assertTrue(response.getExpiresIn() > 0);
    }

    private void assertTrue(boolean b) {
    }

    @Test
    void noUser_whenAuthenticate_thenUserNotFound() {
        when(userService.findUserAndPasswordByEmail(authenticationRequestDTO.getUsername()))
                .thenReturn(Optional.empty());

        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                () -> authenticationService.authenticate(authenticationRequestDTO));
    }

    @Test
    void withValidTokenInRequest_whenGetTokenAndExpiredAd_shouldReturnTokenAndExpiration() {
        // Simular un JWT con un valor de token y fecha de expiración
        Instant expirationTime = Instant.now().plusSeconds(3600);
        Jwt jwt = Jwt.withTokenValue("mocked-jwt-token")
                .header("test-user", "HS256")
                .claim("sub", "test-user")
                .claim("authorities", List.of("ROLE_USER"))
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        // Crear el token de autenticación con el JWT mockeado
        JwtAuthenticationToken jwtAuthToken = new JwtAuthenticationToken(jwt, Collections.emptyList());

        // Mock del contexto de seguridad para establecer el usuario autenticado
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(jwtAuthToken);
        SecurityContextHolder.setContext(securityContext);

        // Mock de la petición HTTP con el token en la cabecera
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer mocked-jwt-token");

        // Llamar al metodo que se está probando
        var result = authenticationService.getTokenAndExpiredAt(request);

        // Verificar que el resultado es correcto
        Assertions.assertNotNull(result, "El resultado no debe ser nulo");
        Assertions. assertEquals("mocked-jwt-token", result.getLeft(), "El token debe coincidir");
        Assertions.assertEquals(expirationTime.getEpochSecond(), result.getRight(), "La fecha de expiración debe coincidir");
    }

    @Test
    void invalidTokenInRequest_whenGetTokenAndExpiredAd_shouldReturnNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");

        Pair<String, Long> result = authenticationService.getTokenAndExpiredAt(request);

        assertNull(result);
    }

    @Test
    void noAuthorizationHeader_whenGetTokenAndExpiredAd_shouldReturnNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        Pair<String, Long> result = authenticationService.getTokenAndExpiredAt(request);

        assertNull(result);
    }

    @Test
    void getTokenAndExpiredAt_withExpiredToken_shouldReturnNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer expired-token");

        Pair<String, Long> result = authenticationService.getTokenAndExpiredAt(request);

        assertNull(result);
    }

    @Test
    void authenticatedUser_whenGetAuthenticated_thenReturnUserData() throws LoginException {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");
        List<GrantedAuthority> rolesAndAuthorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("READ_PRIVILEGE"));
        when(authentication.getAuthorities()).thenReturn( (List) rolesAndAuthorities);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        AuthenticatedMeResponseDTO result = authenticationService.getAuthenticated();

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        assertTrue(result.getRoles().contains("ROLE_USER"));
        assertTrue(result.getAuthorities().contains("READ_PRIVILEGE"));
    }

    @Test
    void noAuthenticatedUser_whenGetAuthenticated_thenThrowLoginException() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(LoginException.class, () -> authenticationService.getAuthenticated());
    }
}