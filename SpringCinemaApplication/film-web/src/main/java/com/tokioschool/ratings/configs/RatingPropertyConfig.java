package com.tokioschool.ratings.configs;

import com.tokioschool.configs.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Clase de configuración para cargar las propiedades relacionadas con el sistema de calificaciones.
 *
 * Esta clase utiliza anotaciones de Spring para configurar y habilitar las propiedades
 * definidas en un archivo YAML específico.
 *
 * Anotaciones utilizadas:
 * - `@Configuration`: Marca esta clase como una clase de configuración de Spring.
 * - `@PropertySource`: Especifica la ubicación del archivo de propiedades YAML, su codificación
 *   y la fábrica personalizada para cargarlo.
 * - `@EnableConfigurationProperties`: Habilita la vinculación de las propiedades definidas
 *   en la clase `RatingProperty`.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
@PropertySource(
        value = "classpath:ratings-auth.yml", // Ruta del archivo de propiedades YAML
        encoding = "UTF-8", // Codificación del archivo
        factory = YamlPropertySourceFactory.class // Fábrica personalizada para cargar propiedades YAML
)
@EnableConfigurationProperties(RatingProperty.class)
public class RatingPropertyConfig {
}