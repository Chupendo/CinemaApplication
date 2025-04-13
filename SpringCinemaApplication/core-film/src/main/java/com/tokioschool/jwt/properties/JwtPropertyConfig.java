package com.tokioschool.jwt.properties;

import com.tokioschool.configs.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuración de propiedades para JWT.
 *
 * Esta clase configura las propiedades relacionadas con JWT utilizando un archivo
 * de propiedades en formato YAML. Habilita la carga de las propiedades definidas
 * en la clase \{@link JwtProperty\}.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
@PropertySource(
        value = "classpath:json-web-token.yml", // Ruta del archivo de propiedades YAML
        encoding = "UTF-8", // Codificación del archivo
        factory = YamlPropertySourceFactory.class // Fábrica personalizada para cargar propiedades YAML
)
@EnableConfigurationProperties(JwtProperty.class) // Habilita la clase JwtProperty como fuente de propiedades
public class JwtPropertyConfig {

}