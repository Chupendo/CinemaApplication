package com.tokioschool.securities.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;

/**
 * Configuración para integrar el dialecto de Spring Security con Thymeleaf.
 *
 * Esta clase define un bean que habilita el uso de las características de seguridad
 * de Spring Security en las plantillas Thymeleaf.
 *
 * Anotaciones utilizadas:
 * - `@Configuration`: Marca esta clase como una clase de configuración de Spring.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
public class ThymeleafDialectConfig {

    /**
     * Define un bean para el dialecto de Spring Security en Thymeleaf.
     *
     * Este bean permite utilizar etiquetas y expresiones específicas de Spring Security
     * en las plantillas Thymeleaf, como `sec:authorize` y `sec:authentication`.
     *
     * @return Una instancia de `SpringSecurityDialect`.
     */
    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }
}