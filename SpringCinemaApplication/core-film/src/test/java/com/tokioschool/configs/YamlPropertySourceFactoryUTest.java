package com.tokioschool.configs;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ContextConfiguration(classes = {YamlPropertySourceFactory.class, YamlPropertyConfig.class})
class YamlPropertySourceFactoryUTest {

    /**
     * Test para verificar la creación de un PropertySource a partir de un recurso simulado.
     *
     * @throws IOException
     */
    @Test
    void givenResourceMock_whenReadPropertySourceYML_thenOK() throws IOException {
        // Mock del recurso
        Resource mockResource = Mockito.mock(Resource.class);
        EncodedResource encodedResource = new EncodedResource(mockResource);

        // YAML simulado en memoria
        String yamlContent = "key: value";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(yamlContent.getBytes(StandardCharsets.UTF_8));

        // Configura el mock para devolver el stream y nombre
        when(mockResource.getInputStream()).thenReturn(inputStream);
        when(mockResource.getFilename()).thenReturn("json-web-token.yml");

        // Clase a probar
        YamlPropertySourceFactory factory = new YamlPropertySourceFactory();

        // Llamada al método
        PropertySource<?> propertySource = factory.createPropertySource("json-web-token", encodedResource);

        // Verificaciones
        assertNotNull(propertySource);
        assertEquals("value", propertySource.getProperty("key"));
    }

    /**
     * Test para verificar la creación de un PropertySource a partir de un recurso real.
     *
     * Required:
     * src/
     *  └── test/
     *      └── resources/
     *          └── json-web-token.yml
     *
     *
     * @throws IOException
     */
    @Test
    void givenResourceWithMock_whenReadPropertySourceYML_thenOk() throws IOException {
        // Cargar el recurso real desde el classpath
        Resource resource = new ClassPathResource("json-web-token.yml");
        EncodedResource encodedResource = new EncodedResource(resource);

        // Clase a probar
        YamlPropertySourceFactory factory = new YamlPropertySourceFactory();

        // Llamada al metodo
        PropertySource<?> propertySource = factory.createPropertySource("json-web-token", encodedResource);

        // Verificaciones
        assertNotNull(propertySource);
        assertEquals("secretTest", propertySource.getProperty("jwt.secret"));
    }
}