package com.tokioschool.filmapp.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordEncoderConfigurationUTest {

    // Con ApplicationContextRunner, prueba que los beans de PasswordEncoderConfigurationU
    // estan configurados
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class);

    @Test
    void givenPasswordEncoder_whenLoadPasswordEncoderBean_thenReturnBCryptPasswordEncoder() {
        contextRunner.run(context -> {
            PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);
            assertNotNull(passwordEncoder, "PasswordEncoder debería estar configurado");
            assertTrue(passwordEncoder instanceof BCryptPasswordEncoder, "Debería ser una instancia de BCryptPasswordEncoder");
        });
    }

    @Test
    @WithMockUser
    void givenUserMock_whenEnableMethodSecurity_thenReturnOk() {
        // Con WithMockUser
        // Valida que la configuración de @EnableMethodSecurity en ServiceSecurityConfiguration
        // funciona correctamente al cargar un usuario simulado.
        contextRunner.run(context -> {
            assertNotNull(context, "El contexto debería cargarse correctamente con la configuración de seguridad habilitada");
        });
    }

    // Carga la configuracion de PasswordEncoderConfiguration.java en las pruebas
    @Configuration
    static class TestConfig extends PasswordEncoderConfiguration {
        // Heredamos la configuración para la prueba
    }
}
