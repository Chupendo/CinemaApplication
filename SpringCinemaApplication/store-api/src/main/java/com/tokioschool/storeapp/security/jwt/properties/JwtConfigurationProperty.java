package com.tokioschool.storeapp.security.jwt.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Propiedades de configuración para JWT.
 *
 * Esta clase define las propiedades relacionadas con la configuración de JWT,
 * como el secreto utilizado para firmar los tokens y la duración de expiración
 * de los mismos. Estas propiedades se cargan desde el archivo de configuración
 * de la aplicación utilizando el prefijo `application.jwt`.
 *
 * Notas:
 * - Es una clase de tipo `record`, lo que la hace inmutable y adecuada para
 *   almacenar datos de configuración.
 * - Utiliza la anotación `@ConfigurationProperties` para mapear las propiedades
 *   del archivo de configuración.
 *
 * @param secret El secreto utilizado para firmar los tokens JWT.
 * @param expiration La duración de expiración de los tokens JWT.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@ConfigurationProperties(prefix = "application.jwt")
public record JwtConfigurationProperty(String secret, Duration expiration) {
}