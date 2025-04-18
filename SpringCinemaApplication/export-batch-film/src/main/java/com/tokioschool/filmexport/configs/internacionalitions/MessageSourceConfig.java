package com.tokioschool.filmexport.configs.internacionalitions;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Configuración para la gestión de mensajes internacionalizados.
 *
 * Esta clase configura un `MessageSource` que permite la resolución de mensajes
 * desde archivos de propiedades para soportar la internacionalización (i18n) en la aplicación.
 *
 * Anotaciones utilizadas:
 * - `@Configuration`: Marca esta clase como una clase de configuración de Spring.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
public class MessageSourceConfig {

    /**
     * Crea un bean de `MessageSource` para la resolución de mensajes.
     *
     * Este metodo configura un `ResourceBundleMessageSource` que busca los mensajes
     * en los archivos de propiedades especificados. También establece la codificación
     * predeterminada y permite usar el código del mensaje como valor predeterminado
     * si no se encuentra una traducción.
     *
     * @return Una instancia configurada de `ResourceBundleMessageSource`.
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.addBasenames("message/messages"); // sin .properties
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }
}