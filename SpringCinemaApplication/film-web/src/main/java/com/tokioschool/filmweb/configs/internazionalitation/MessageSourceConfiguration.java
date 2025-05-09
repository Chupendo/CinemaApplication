package com.tokioschool.filmweb.configs.internazionalitation;

import com.transferwise.icu.ICUMessageSource;
import com.transferwise.icu.ICUReloadableResourceBundleMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Configuración de la fuente de mensajes para la internacionalización de la aplicación.
 *
 * Esta clase define los beans necesarios para gestionar los mensajes de la aplicación
 * utilizando archivos de propiedades y soporte para pluralización con ICU.
 *
 * Anotaciones utilizadas:
 * - `@Configuration`: Marca esta clase como una clase de configuración de Spring.
 *
 * @author andres.rpenuela
 * @version 1.o
 */
@Configuration
public class MessageSourceConfiguration {

    /**
     * Define el bean principal para la fuente de mensajes de la aplicación.
     *
     * Este metodo configura un `ResourceBundleMessageSource` que utiliza un
     * `ICUMessageSource` como fuente principal para soportar pluralización.
     *
     * Nota: Requiere comentar las propiedades relacionadas con `message source` en
     * el archivo `application.properties`:
     * - `spring.messages.basename=message/message`
     * - `spring.messages.fallback-to-system-locale=true`
     *
     * @return Una instancia de `MessageSource` configurada.
     */
    @Bean
    @Primary
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        // Configura el ICUMessageSource como fuente principal para soportar pluralización
        messageSource.setParentMessageSource(icuMessageSource());

        /*
        // Definición de las fuentes de mensajes
        messageSource.addBasenames(
                "message/message", // Mensajes i18n propios (sin soporte ICU en ResourceBundleMessageSource)
                "org.hibernate.validator.ValidationMessages" // Mensajes i18n de validación de Jakarta
        );

        // Configuración adicional
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.displayName());
        */
        return messageSource;
    }

    /**
     * Define un bean para la fuente de mensajes con soporte ICU.
     *
     * Este metodo configura un `ICUReloadableResourceBundleMessageSource` que permite
     * manejar pluralización y otros formatos avanzados en los mensajes.
     *
     * Documentación adicional:
     * - Soporte de Spring MessageSource ICU: {@see https://github.com/transferwise/spring-icu}
     * - Ejemplo: {@see https://lokalise.com/blog/spring-boot-internationalization/}
     *
     * @return Una instancia de `ICUMessageSource` configurada.
     */
    //@Bean // Debe ser un Bean si "messageSource" es gestionado por Spring
    public ICUMessageSource icuMessageSource() {
        ICUReloadableResourceBundleMessageSource icuMessageSource = new ICUReloadableResourceBundleMessageSource();

        // Configuración de las fuentes de mensajes con soporte ICU
        icuMessageSource.setBasenames(
                "classpath:message/message", // Mensajes i18n propios
                "classpath:org/hibernate/validator/ValidationMessages", // Mensajes de validación de Hibernate
                "classpath:icu-message/res" // Mensajes i18n adicionales
        );

        // Configuración adicional (comentada por defecto)
        // icuMessageSource.setUseCodeAsDefaultMessage(true);
        // icuMessageSource.setDefaultEncoding(StandardCharsets.UTF_8.displayName());

        return icuMessageSource;
    }
}