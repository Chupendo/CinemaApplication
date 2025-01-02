package com.tokioschool.filmapp.services.auth.impl;

import com.tokioschool.filmapp.dto.auth.AuthenticatedMeResponseDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationRequestDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationResponseDTO;
import com.tokioschool.filmapp.services.jwt.JwtService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AuthenticationServiceImplUTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void givenUserDetails_whenAuthenticate_thenReturnJwt() {
        UserDetails userDetails = User.builder()
                .username("andres")
                .password("123")
                .authorities("ROLE_USER")
                .build();

        AuthenticationRequestDTO authenticationRequestDTO = AuthenticationRequestDTO.builder()
                .username(userDetails.getUsername())
                .password(userDetails.getPassword())
                .build();

        Jwt mockJwt = Jwt.withTokenValue("mock-token")
                .header(userDetails.getUsername(), "HS256")
                .claim("sub", "test-user")
                .claim("authorities", List.of("ROLE_USER"))
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(
                        userDetails,
                        "123",
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                ));

        Mockito.when( jwtService.generateToken(userDetails) ).thenReturn(mockJwt);

        // Act
        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.authenticate(authenticationRequestDTO);

        // Assertions
        Assertions.assertThat( authenticationResponseDTO ).isNotNull();
    }

    @Test
    void givenUserDetails_whenGetAuthenticated_thenReturnOk() {
        // TODO
        UserDetails userDetails = User.builder()
                .username("andres")
                .password("123")
                .authorities("ROLE_USER")
                .build();

        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(
                userDetails,
                "123",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        ));

        AuthenticatedMeResponseDTO authenticatedMeResponseDTO = authenticationService.getAuthenticated();

        Assertions.assertThat(authenticatedMeResponseDTO).isNotNull()
                .returns(userDetails.getUsername(),AuthenticatedMeResponseDTO::getUsername);
    }
}