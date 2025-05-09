package com.tokioschool.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Clase de configuración para cargar propiedades desde archivos YAML.
 * Utiliza la anotación {@link ConfigurationProperties} para mapear las propiedades
 * con el prefijo "yaml" a esta clase.
 *
 * Esta clase está marcada como una configuración de Spring mediante la anotación {@link Configuration}.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "yaml")
public class YamlPropertyConfig {
}