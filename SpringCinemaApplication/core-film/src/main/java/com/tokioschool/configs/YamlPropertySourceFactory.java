package com.tokioschool.configs;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Properties;


/**
 * Fábrica personalizada para cargar propiedades desde archivos YAML.
 * Implementa la interfaz {@link PropertySourceFactory} para permitir el uso de archivos YAML
 * como fuentes de propiedades en configuraciones de Spring.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

    /**
     * Crea una fuente de propiedades a partir de un recurso codificado YAML.
     *
     * @param name El nombre de la fuente de propiedades (puede ser nulo).
     * @param encodedResource El recurso codificado que contiene el archivo YAML.
     * @return Una instancia de {@link PropertySource} que contiene las propiedades cargadas.
     * @throws IOException Si ocurre un error al leer el recurso.
     */
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource)
            throws IOException {
        // Crea una instancia de YamlPropertiesFactoryBean para procesar el archivo YAML
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(encodedResource.getResource());

        // Obtiene las propiedades del archivo YAML
        Properties properties = factory.getObject();

        // Retorna una fuente de propiedades basada en las propiedades cargadas
        return new PropertiesPropertySource(encodedResource.getResource().getFilename(), properties);
    }
}