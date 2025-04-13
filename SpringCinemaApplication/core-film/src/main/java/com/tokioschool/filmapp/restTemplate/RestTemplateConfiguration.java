package com.tokioschool.filmapp.restTemplate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuración para el cliente REST utilizado en la aplicación.
 *
 * Esta clase define un bean de \{@link RestTemplate\} que se puede inyectar en otras partes
 * de la aplicación para realizar solicitudes HTTP. Proporciona una instancia básica de
 * \{@link RestTemplate\} con configuración predeterminada.
 *
 * @version 1.0
 * @author
 */
@Configuration
public class RestTemplateConfiguration {

    /**
     * Define un bean de \{@link RestTemplate\} con configuración predeterminada.
     *
     * Este bean se utiliza para realizar solicitudes HTTP en la aplicación.
     *
     * @return Una instancia de \{@link RestTemplate\}.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}