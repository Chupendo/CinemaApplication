package com.tokioschool.ratingapp.auth.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
@RequiredArgsConstructor
public class AuthenticationManagerConfiguration {
    // se inyecta de forma autoamica si se usa "formLogin" en el filter chain
    // se requiere definir un bean de Authentication Provider
    // por eso no es para auth por contraseña
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // Usamos el AuthenticationConfiguration para obtener el AuthenticationManager
        return authenticationConfiguration.getAuthenticationManager();
    }
}
