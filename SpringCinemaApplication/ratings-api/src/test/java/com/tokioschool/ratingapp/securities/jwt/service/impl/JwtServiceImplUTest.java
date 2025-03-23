package com.tokioschool.ratingapp.securities.jwt.service.impl;

import com.tokioschool.ratingapp.securities.jwt.properties.JwtConfigurationProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class JwtServiceImplUTest {
/*   @Mock
    private UserDetails userDetails;

    @Mock
    private JwtConfigurationProperty jwtConfigurationProperty;
    @Mock
    private NimbusJwtEncoder jwtEncoder;

    @InjectMocks
    private JwtServiceImpl jwtService;

    @Test
    void generateToken_withValidUserDetails_shouldReturnJwt() throws AccessDeniedException {
        List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        when(userDetails.getUsername()).thenReturn("testUser");
        when(userDetails.getAuthorities()).thenReturn((List) roles);
        when(jwtConfigurationProperty.expiration()).thenReturn(Duration.ofHours(1));

        Jwt expectedJwt = mock(Jwt.class);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(expectedJwt);

        Jwt jwt = jwtService.generateToken(userDetails);

        assertNotNull(jwt);
        assertEquals(expectedJwt, jwt);
    }

    @Test
    void generateToken_withNoAuthorities_shouldThrowAccessDeniedException() {
        when(userDetails.getAuthorities()).thenReturn(List.of());

        assertThrows(AccessDeniedException.class, () -> jwtService.generateToken(userDetails));
    }

    @Test
    void generateToken_withNullAuthorities_shouldThrowAccessDeniedException() {
        when(userDetails.getAuthorities()).thenReturn(null);

        assertThrows(AccessDeniedException.class, () -> jwtService.generateToken(userDetails));
    }

    @Test
    void generateToken_withNullUserDetails_shouldThrowUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> jwtService.generateToken(null));
    }*/
}