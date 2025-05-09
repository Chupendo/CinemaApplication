package com.tokioschool.storeapp.security.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Configuración de seguridad para la aplicación.
 *
 * Esta clase habilita la seguridad a nivel de métodos en la aplicación utilizando
 * las anotaciones de Spring Security.
 *
 * Notas:
 * - Es anotada con `@Configuration` para indicar que es una clase de configuración de Spring.
 * - Utiliza `@EnableMethodSecurity` para activar la seguridad basada en anotaciones en los métodos.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {
}