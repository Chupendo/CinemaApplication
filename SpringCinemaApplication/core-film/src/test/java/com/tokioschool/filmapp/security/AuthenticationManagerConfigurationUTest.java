package com.tokioschool.filmapp.security;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuthenticationManagerConfigurationUTest {


    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class);

    @Test
    void givenAuthenticationMock_whenLoadAuthenticationManager_thenReturnOk() {
        contextRunner.run(context -> {
            AuthenticationManager authenticationManager = context.getBean(AuthenticationManager.class);
            assertNotNull(authenticationManager, "AuthenticationManager debería estar configurado correctamente");
        });
    }

    @Configuration
    static class TestConfig {

        @Bean
        public AuthenticationConfiguration authenticationConfiguration() {
            // Simulamos un AuthenticationConfiguration para la prueba
            return Mockito.mock(AuthenticationConfiguration.class);
        }

        @Bean
        public AuthenticationManagerConfiguration authenticationManagerConfiguration() {
            return new AuthenticationManagerConfiguration();
        }
    }
}