package com.tokioschool.securities.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración de codificación de contraseñas.
 *
 * Esta clase define la configuración necesaria para la codificación de contraseñas
 * utilizando el algoritmo BCrypt, así como la habilitación de la seguridad a nivel de métodos.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
public class PasswordEncoderConfiguration {

    /**
     * Define un bean para la codificación de contraseñas.
     *
     * Este método proporciona una instancia de \{@link BCryptPasswordEncoder\} que se utiliza
     * para codificar contraseñas de manera segura.
     *
     * @return una instancia de \{@link PasswordEncoder\} basada en BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuración de seguridad a nivel de servicios.
     *
     * Esta clase interna habilita la seguridad a nivel de métodos mediante la anotación
     * \{@link EnableMethodSecurity\}.
     */
    @Configuration
    @EnableMethodSecurity
    public static class ServiceSecurityConfiguration {
    }
}