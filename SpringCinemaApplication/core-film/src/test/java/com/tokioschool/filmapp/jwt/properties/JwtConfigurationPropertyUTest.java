package com.tokioschool.filmapp.jwt.properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

@ActiveProfiles("test")
public class JwtConfigurationPropertyUTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withPropertyValues(
                    "application.jwt.secret=test-secret",
                    "application.jwt.expiration=PT30M"
            )
            .withUserConfiguration(TestConfig.class);

    @Test
    void givenJwtProperties_whenLoadJwtConfigurationProperties_thenReturnOk() {
        contextRunner.run(context -> {
            JwtConfigurationProperty jwtConfigurationProperty = context.getBean(JwtConfigurationProperty.class);

            // Assert
            Assertions.assertEquals("test-secret", jwtConfigurationProperty.secret());
            Assertions.assertEquals(Duration.ofMinutes(30), jwtConfigurationProperty.expiration());
        });
    }

    @Configuration
    @EnableConfigurationProperties(JwtConfigurationProperty.class)
    static class TestConfig {
        // Configuración básica para habilitar las propiedades
    }
}
