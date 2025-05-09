package com.tokioschool.filmapp.security.confings;

import com.tokioschool.jwt.properties.JwtProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
class JwtConfigurationUTest {

    @Mock
    private JwtProperty jwtProperty;

    private JwtConfiguration jwtConfiguration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtConfiguration = new JwtConfiguration(jwtProperty);
    }

    @Test
    void hmacJwtDecoder_withValidSecret_shouldReturnDecoder() {
        when(jwtProperty.secret()).thenReturn("validSecret");

        NimbusJwtDecoder decoder = jwtConfiguration.hmacJwtDecoder();

        assertNotNull(decoder);
    }

    @Test
    void hmacJwtDecoder_withNullSecret_shouldThrowException() {
        when(jwtProperty.secret()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> jwtConfiguration.hmacJwtDecoder());
    }

    @Test
    void hmacJwtDecoder_withEmptySecret_shouldThrowException() {
        when(jwtProperty.secret()).thenReturn("");

        assertThrows(IllegalArgumentException.class, () -> jwtConfiguration.hmacJwtDecoder());
    }

    @Test
    void nimbusJwtEncoder_withValidSecret_shouldReturnEncoder() {
        when(jwtProperty.secret()).thenReturn("validSecret");

        NimbusJwtEncoder encoder = jwtConfiguration.nimbusJwtEncoder();

        assertNotNull(encoder);
    }

    @Test
    void nimbusJwtEncoder_withNullSecret_shouldThrowException() {
        when(jwtProperty.secret()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> jwtConfiguration.nimbusJwtEncoder());
    }

    @Test
    void nimbusJwtEncoder_withEmptySecret_shouldThrowException() {
        when(jwtProperty.secret()).thenReturn("");

        assertThrows(IllegalArgumentException.class, () -> jwtConfiguration.nimbusJwtEncoder());
    }
}