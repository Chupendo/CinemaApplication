package com.tokioschool.store.restClients;

import com.tokioschool.store.authentications.StoreAuthenticationService;
import com.tokioschool.store.properties.StorePropertiesFilm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/**
 * Configuración de los clientes REST para interactuar con la API de la tienda.
 *
 * Esta clase define dos clientes REST, uno para el consumidor y otro para el productor,
 * con sus respectivas configuraciones de autenticación y encabezados.
 *
 * Anotaciones:
 * - {@link Configuration}: Indica que esta clase contiene definiciones de beans de Spring.
 * - {@link RequiredArgsConstructor}: Genera un constructor con los argumentos requeridos.
 * - {@link Slf4j}: Habilita el registro de logs utilizando SLF4J.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class StoreRestClientConfiguration {

    private final StorePropertiesFilm storePropertiesFilm;
    private final StoreAuthenticationService storeAuthenticationService;

    @Value("${application.store.login.users[0].username}")
    private String consumer;

    @Value("${application.store.login.users[1].username}")
    private String producer;

    /**
     * Define un cliente REST para el consumidor.
     *
     * Este cliente utiliza la URL base definida en las propiedades de la tienda y
     * agrega un token de acceso en el encabezado de autorización para cada solicitud.
     *
     * @return Una instancia configurada de {@link RestClient}.
     */
    @Bean
    @Qualifier("restClientConsumer")
    public RestClient restConsumerClient() {
        return RestClient.builder()
                .baseUrl(storePropertiesFilm.baseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .requestInitializer(request -> {
                    log.info("Adding secret to request as {}", consumer);
                    request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(storeAuthenticationService.getAccessToken(consumer)));
                })
                .build();
    }

    /**
     * Define un cliente REST para el productor.
     *
     * Este cliente utiliza la URL base definida en las propiedades de la tienda y
     * agrega un token de acceso en el encabezado de autorización para cada solicitud.
     *
     * @return Una instancia configurada de {@link RestClient}.
     */
    @Bean
    @Qualifier("restClientProducer")
    public RestClient restProducerClient() {
        return RestClient.builder()
                .baseUrl(storePropertiesFilm.baseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .requestInitializer(request -> {
                    log.info("Adding secret to request as {}", producer);
                    request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(storeAuthenticationService.getAccessToken(producer)));
                })
                .build();
    }
}