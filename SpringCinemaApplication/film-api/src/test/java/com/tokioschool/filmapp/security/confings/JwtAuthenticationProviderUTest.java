package com.tokioschool.filmapp.security.confings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class JwtAuthenticationProviderUTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtDecoder jwtDecoder;

    private AuthenticationProvider authenticationProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationProvider = new JwtAuthenticationProvider(userDetailsService, jwtDecoder);
    }

    @Test
    void authenticate_withValidToken_shouldReturnAuthentication() {
        String token = "validToken";
        Jwt jwt = mock(Jwt.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(jwtDecoder.decode(token)).thenReturn(jwt);
        when(jwt.getSubject()).thenReturn("username");
        when(userDetailsService.loadUserByUsername("username")).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(null);

        var authentication = authenticationProvider.authenticate(new BearerTokenAuthenticationToken(token));

        assertNotNull(authentication);
        assertEquals(userDetails, authentication.getPrincipal());
        assertEquals(token, authentication.getCredentials());
    }

    @Test
    void authenticate_withInvalidToken_shouldThrowException() {
        String token = "invalidToken";

        when(jwtDecoder.decode(token)).thenThrow(new JwtException("Invalid token"));

        assertThrows(RuntimeException.class, () ->
                authenticationProvider.authenticate(new BearerTokenAuthenticationToken(token))
        );
    }

    @Test
    void authenticate_withNullToken_shouldThrowException() {
        assertThrows(RuntimeException.class, () ->
                authenticationProvider.authenticate(new BearerTokenAuthenticationToken(null))
        );
    }

    @Test
    void supports_withBearerTokenAuthenticationToken_shouldReturnTrue() {
        assertTrue(authenticationProvider.supports(BearerTokenAuthenticationToken.class));
    }

    @Test
    void supports_withOtherAuthenticationType_shouldReturnFalse() {
        assertFalse(authenticationProvider.supports(UsernamePasswordAuthenticationToken.class));
    }
}