package com.tokioschool.filmapp.security;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
class AuthenticationManagerConfigurationUTest {

    @Test
    void givenAuthenticationMock_whenLoadAuthenticationManager_thenReturnOk() {
        // Crear un contexto manualmente
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            // Registrar la clase de configuración manualmente
            context.register(AuthenticationManagerConfiguration.class);
            context.register(AuthenticationConfiguration.class); // Necesario para `AuthenticationConfiguration`
            context.refresh();

            // Obtener el bean del contexto
            AuthenticationManager authenticationManager = context.getBean(AuthenticationManager.class);

            // Verificar que no es null
            assertNotNull(authenticationManager, "El AuthenticationManager no debería ser null");
        }
    }
}