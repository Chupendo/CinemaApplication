package com.tokioschool.filmapp.jwt.properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = JwtConfigurationUTest.TestConfig.class)
@ActiveProfiles("test")
class JwtConfigurationUTest {
    @Mock
    private JwtConfigurationProperty jwtConfigurationProperty;

    @Configuration
    static class TestConfig {
        @Bean
        JwtConfigurationProperty jwtConfigurationProperty() {
            return Mockito.mock(JwtConfigurationProperty.class);
        }

        @Bean
        JwtConfiguration jwtConfiguration(JwtConfigurationProperty jwtConfigurationProperty) {
            return new JwtConfiguration(jwtConfigurationProperty);
        }
    }

    @Test
    void givenASecret_whenCreateBeanNimbusJwtEncoder_thenReturnInstanceJwtEncoder() {
        // Arrange
        Mockito.when(jwtConfigurationProperty.secret()).thenReturn("test-secret");

        JwtConfiguration jwtConfiguration = new JwtConfiguration(jwtConfigurationProperty);

        // Act
        JwtEncoder jwtEncoder = jwtConfiguration.nimbusJwtEncoder();

        // Assert
        Assertions.assertNotNull(jwtEncoder, "NimbusJwtEncoder debería estar correctamente configurado");
    }

    @Test
    void givenASecret_whenCreateBeanNimbusJwtDecoder_thenReturnInstanceJJwtDecoder() {
        // Arrange
        Mockito.when(jwtConfigurationProperty.secret()).thenReturn("test-secret");

        JwtConfiguration jwtConfiguration = new JwtConfiguration(jwtConfigurationProperty);

        // Act
        JwtDecoder jwtDecoder = jwtConfiguration.nimbusJwtDecoder();

        // Assert
        Assertions.assertNotNull(jwtDecoder, "NimbusJwtDecoder debería estar correctamente configurado");
    }
}