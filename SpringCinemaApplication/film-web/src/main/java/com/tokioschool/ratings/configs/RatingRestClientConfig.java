package com.tokioschool.ratings.configs;

import com.tokioschool.ratings.authentications.RatingAuth2;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/**
 * Clase de configuración para el cliente REST utilizado en el sistema de calificaciones.
 *
 * Esta clase define un bean que configura un cliente REST con las propiedades necesarias
 * para realizar solicitudes autenticadas al sistema de calificaciones.
 *
 * Anotaciones utilizadas:
 * - `@Configuration`: Marca esta clase como una clase de configuración de Spring.
 * - `@RequiredArgsConstructor`: Genera un constructor con los argumentos requeridos
 *   para las dependencias inyectadas.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
@RequiredArgsConstructor
public class RatingRestClientConfig {

    /** Propiedades de configuración relacionadas con el sistema de calificaciones. */
    private final RatingProperty ratingProperty;

    /** Servicio de autenticación para obtener el token de acceso. */
    private final RatingAuth2 ratingAuth2;

    /**
     * Define un bean para el cliente REST configurado con autenticación.
     *
     * Este cliente REST utiliza la URL base definida en las propiedades de configuración
     * y agrega un encabezado de autorización con un token de acceso válido.
     *
     * @return Una instancia configurada de `RestClient`.
     */
    @Bean("ratingRestClientCredentials")
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(ratingProperty.baseUrl()) // Establece la URL base del cliente REST
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // Configura el encabezado Content-Type
                .requestInitializer(request ->
                        request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(ratingAuth2.getTokenAccess())) // Agrega el encabezado de autorización
                ).build();
    }
}