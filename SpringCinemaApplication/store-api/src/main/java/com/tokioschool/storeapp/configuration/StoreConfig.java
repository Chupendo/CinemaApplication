package com.tokioschool.storeapp.configuration;

import com.tokioschool.storeapp.configuration.properties.StoreConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Clase de configuración principal para la aplicación de la tienda.
 *
 * Esta clase se encarga de habilitar las propiedades de configuración definidas
 * en {@link StoreConfigurationProperties} mediante la anotación {@link EnableConfigurationProperties}.
 *
 * Anotaciones:
 * - {@link Configuration}: Marca esta clase como una clase de configuración de Spring.
 * - {@link EnableConfigurationProperties}: Habilita el soporte para las propiedades de configuración
 *   especificadas en la clase {@link StoreConfigurationProperties}.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
@EnableConfigurationProperties(value = StoreConfigurationProperties.class)
public class StoreConfig {
}