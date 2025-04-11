package com.tokioschool.storeapp.security.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

/**
 * Configuración para el `AuthenticationManager` en la aplicación.
 *
 * Esta clase define un bean de Spring para proporcionar una instancia de `AuthenticationManager`,
 * que es utilizada por Spring Security para gestionar la autenticación.
 *
 * Notas:
 * - Utiliza la clase `AuthenticationConfiguration` para obtener el `AuthenticationManager`.
 * - Es anotada con `@Configuration` para indicar que es una clase de configuración de Spring.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
public class AuthenticationManagerConfiguration {

    /**
     * Define un bean de `AuthenticationManager`.
     *
     * Este método crea y expone un bean de `AuthenticationManager` utilizando la configuración
     * proporcionada por `AuthenticationConfiguration`.
     *
     * @param authenticationConfiguration Configuración de autenticación proporcionada por Spring Security.
     * @return Una instancia de `AuthenticationManager`.
     * @throws Exception Si ocurre un error al obtener el `AuthenticationManager`.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}