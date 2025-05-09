package com.tokioschool.store.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de propiedades para la aplicación.
 *
 * Esta clase habilita y configura las propiedades específicas de la aplicación
 * relacionadas con la gestión de recursos de películas.
 *
 * Anotaciones:
 * - {@link Configuration}: Marca esta clase como una clase de configuración de Spring.
 * - {@link EnableConfigurationProperties}: Habilita la vinculación de las propiedades definidas
 *   en la clase {@link StorePropertiesFilm}.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
@EnableConfigurationProperties(StorePropertiesFilm.class)
public class StorePropertiesConfig {
}