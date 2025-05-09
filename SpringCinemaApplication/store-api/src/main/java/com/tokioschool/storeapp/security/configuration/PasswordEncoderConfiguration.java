package com.tokioschool.storeapp.security.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración para el codificador de contraseñas en la aplicación.
 *
 * Esta clase define un bean de Spring para proporcionar una instancia de `PasswordEncoder`,
 * que se utiliza para codificar contraseñas de manera segura utilizando el algoritmo BCrypt.
 *
 * Notas:
 * - Es anotada con `@Configuration` para indicar que es una clase de configuración de Spring.
 * - Utiliza `BCryptPasswordEncoder` como implementación del codificador de contraseñas.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
public class PasswordEncoderConfiguration {

    /**
     * Define un bean de `PasswordEncoder`.
     *
     * Este metodo crea y expone un bean de `PasswordEncoder` utilizando la implementación
     * `BCryptPasswordEncoder`, que aplica un algoritmo de hash seguro para las contraseñas.
     *
     * @return Una instancia de `PasswordEncoder` basada en `BCryptPasswordEncoder`.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}