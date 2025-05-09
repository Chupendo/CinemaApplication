package com.tokioschool.ratings.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Clase de configuración para las propiedades relacionadas con el sistema de calificaciones.
 *
 * Esta clase utiliza `@ConfigurationProperties` para mapear las propiedades definidas
 * en el archivo de configuración de la aplicación con el prefijo `rating`.
 *
 * Anotaciones utilizadas:
 * - `@ConfigurationProperties`: Indica que esta clase contiene propiedades de configuración
 *   que serán inyectadas automáticamente desde el archivo de configuración.
 *
 * @param baseUrl La URL base para las solicitudes relacionadas con el sistema de calificaciones.
 * @param grantType El tipo de concesión utilizado para la autenticación OAuth2.
 * @param client Las credenciales del cliente necesarias para la autenticación.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@ConfigurationProperties(prefix = "rating")
public record RatingProperty(String baseUrl, String grantType, Client client) {

    /**
     * Clase interna que representa las credenciales del cliente.
     *
     * @param user El nombre de usuario del cliente.
     * @param password La contraseña del cliente (codificada en Base64).
     */
    public record Client(String user, String password) {}
}