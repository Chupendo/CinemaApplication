package com.tokioschool.ratingapp.core.openApi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI para la API de Ratings Films.
 *
 * Esta clase define la configuración de OpenAPI para documentar la API, incluyendo
 * información general sobre la API y el esquema de seguridad utilizado.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Ratings Films API",
                version = "1.0",
                description = "API for management Ratings Films"
        )
)
@SecurityScheme(
        name = "auth-openapi",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfiguration {
}