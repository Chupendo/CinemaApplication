package com.tokioschool.store.configs;

import com.tokioschool.store.properties.StorePropertiesFilm;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para las propiedades de la tienda relacionadas con películas.
 *
 * Esta clase configura las propiedades específicas de la tienda utilizando la clase
 * {@link StorePropertiesFilm}.
 *
 * Anotaciones:
 * - {@link Configuration}: Marca esta clase como una clase de configuración de Spring.
 * - {@link EnableConfigurationProperties}: Habilita la vinculación de las propiedades de configuración
 *   definidas en la clase {@link StorePropertiesFilm}.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
@EnableConfigurationProperties(StorePropertiesFilm.class)
public class StoreConfigurationFilm {
}