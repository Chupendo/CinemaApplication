package com.tokioschool.ratings.configs;

import com.tokioschool.configs.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(
        value = "classpath:ratings-auth.yml", // Ruta del archivo de propiedades YAML
        encoding = "UTF-8", // Codificación del archivo
        factory = YamlPropertySourceFactory.class // Fábrica personalizada para cargar propiedades YAML
)
@EnableConfigurationProperties(RatingProperty.class)
public class RatingPropertyConfig {
}
