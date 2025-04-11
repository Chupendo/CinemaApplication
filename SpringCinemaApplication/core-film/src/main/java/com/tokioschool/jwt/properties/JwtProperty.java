package com.tokioschool.jwt.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Propiedades de configuración para JWT.
 *
 * Esta clase almacena las propiedades relacionadas con la configuración de JWT,
 * como la clave secreta y la duración de expiración del token.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperty(
        /**
         * Clave secreta utilizada para firmar y verificar los tokens JWT.
         */
        String secret,

        /**
         * Duración de expiración del token JWT.
         * Define el tiempo después del cual el token ya no será válido.
         */
        Duration expiration) {}