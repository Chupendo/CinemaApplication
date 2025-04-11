package com.tokioschool.ratingapp.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Clase de configuración de la aplicación.
 *
 * Proporciona configuraciones y beans necesarios para el funcionamiento de la aplicación.
 * En este caso, se configura un `WebClient.Builder` para realizar solicitudes HTTP reactivas.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
public class AppConfig {

    /**
     * Crea un bean de `WebClient.Builder`.
     *
     * Este bean se utiliza para construir instancias de `WebClient` para realizar
     * solicitudes HTTP reactivas.
     *
     * @return una instancia de `WebClient.Builder`.
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}