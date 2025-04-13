package com.tokioschool.filmapp.restTemplate;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

/**
 * Configuración para el cliente REST utilizado en la aplicación.
 *
 * Esta clase define un bean de \{@link RestClient\} que se puede inyectar en otras partes
 * de la aplicación para realizar solicitudes HTTP. El bean está configurado como primario
 * y tiene un calificador específico para diferenciarlo de otros posibles clientes REST.
 *
 * @version 1.0
 * @author
 */
@Configuration
public class RestClientConfiguration {

    /**
     * Define un bean de \{@link RestClient\} con configuración predeterminada.
     *
     * Este bean está marcado como primario y tiene el calificador "restClientEmpty".
     * Se utiliza para realizar solicitudes HTTP con un cliente REST básico.
     *
     * @return Una instancia de \{@link RestClient\}.
     */
    @Bean
    @Primary
    @Qualifier("restClientEmpty")
    public RestClient restClient(){
        return RestClient.builder().build();
    }
}