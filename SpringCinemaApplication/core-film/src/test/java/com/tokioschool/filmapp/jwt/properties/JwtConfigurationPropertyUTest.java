package com.tokioschool.filmapp.jwt.properties;

import com.tokioschool.jwt.properties.JwtProperty;
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
                    "jwt.secret=test-secret",
                    "jwt.expiration=PT30M"
            )
            .withUserConfiguration(TestConfig.class);

    @Test
    void givenJwtProperties_whenLoadJwtConfigurationProperties_thenReturnOk() {
        contextRunner.run(context -> {
            JwtProperty jwtConfigurationProperty = context.getBean(JwtProperty.class);

            // Assert
            Assertions.assertEquals("test-secret", jwtConfigurationProperty.secret());
            Assertions.assertEquals(Duration.ofMinutes(30), jwtConfigurationProperty.expiration());
        });
    }

    @Configuration
    @EnableConfigurationProperties(JwtProperty.class)
    static class TestConfig {
        // Configuración básica para habilitar las propiedades
    }
}
