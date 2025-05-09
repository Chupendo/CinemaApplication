package com.tokioschool.store.restClients;

import com.tokioschool.store.authentications.StoreAuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/**
 * Configuración del cliente REST para interactuar con el sistema de la tienda.
 *
 * Esta clase configura un cliente REST personalizado que incluye la URL base y
 * un encabezado de autorización con un token de acceso.
 *
 * Anotaciones:
 * - {@link Configuration}: Marca esta clase como una clase de configuración de Spring.
 * - {@link RequiredArgsConstructor}: Genera un constructor con los argumentos requeridos
 *   para las dependencias inyectadas.
 * - {@link Slf4j}: Proporciona un logger para registrar mensajes de depuración y errores.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class RestClientCostumerConfig {

    /**
     * URL base para el cliente REST.
     *
     * Este valor se obtiene de las propiedades de la aplicación y tiene un valor
     * predeterminado de "http://localhost:9091" si no se especifica.
     */
    @Value("${application.store.base-url:http://localhost:9091}")
    private String baseUrl;

    /**
     * Servicio de autenticación para obtener el token de acceso.
     */
    private final StoreAuthenticationService storeAuthenticationService;

    /**
     * Define un bean para el cliente REST utilizado en la interacción con el sistema de la tienda.
     *
     * Este cliente REST incluye:
     * - La URL base configurada.
     * - Un encabezado predeterminado para el tipo de contenido (JSON).
     * - Un encabezado de autorización que incluye un token de acceso.
     *
     * @return Una instancia configurada de {@link RestClient}.
     */
    @Bean("restClientCostumer")
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .requestInitializer(request ->
                        request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(storeAuthenticationService.getAccessToken()))
                ).build();
    }
}