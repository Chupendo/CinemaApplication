package com.tokioschool.filmapp.core.openApi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI para la documentación de la API.
 *
 * Esta clase define la configuración global de OpenAPI, incluyendo información general
 * sobre la API y el esquema de seguridad utilizado.
 *
 * Anotaciones:
 * - {@link Configuration}: Indica que esta clase es una configuración de Spring.
 * - {@link OpenAPIDefinition}: Proporciona metadatos generales para la documentación de OpenAPI.
 * - {@link SecurityScheme}: Define el esquema de seguridad utilizado en la API.
 *
 * Configuración:
 * - Título: "Film API"
 * - Versión: "1.0"
 * - Descripción: "API for management file film"
 * - Esquema de seguridad: Autenticación HTTP con formato Bearer (JWT).
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
@OpenAPIDefinition(
        info =  @Info(
                title = "Film API",
                version = "1.0",
                description = "API for management file film"
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