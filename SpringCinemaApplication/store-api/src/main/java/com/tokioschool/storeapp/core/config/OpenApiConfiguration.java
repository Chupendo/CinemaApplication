package com.tokioschool.storeapp.core.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI para la documentación de la API.
 *
 * Esta clase configura la documentación de la API utilizando las anotaciones de OpenAPI.
 * Define información general sobre la API y el esquema de seguridad utilizado.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "File Store API", // Título de la API.
                version = "1.0", // Versión de la API.
                description = "Store service to manage file resources" // Descripción de la API.
        )
)
@SecurityScheme(
        name = "auth-openapi", // Nombre del esquema de seguridad.
        type = SecuritySchemeType.HTTP, // Tipo de esquema de seguridad (HTTP).
        bearerFormat = "JWT", // Formato del token (JWT).
        scheme = "bearer" // Esquema de autenticación (Bearer).
)
public class OpenApiConfiguration {
}