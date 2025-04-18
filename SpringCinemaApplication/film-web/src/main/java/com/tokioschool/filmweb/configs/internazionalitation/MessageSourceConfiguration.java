package com.tokioschool.filmweb.configs.internazionalitation;

import com.transferwise.icu.ICUMessageSource;
import com.transferwise.icu.ICUReloadableResourceBundleMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MessageSourceConfiguration {

    /**
     * Gestion propipia de los resources bandle, donde la fuente de los mensajes
     * serán los ficheros de propiedades
     *
     * Requiere comentar el "message source" de application.properties
     *   spring.messages.basename= message/message
     *   spring.messages.fallback-to-system-locale= true
     * @return
     */
    @Bean
    @Primary
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        // se carga un icu para soportar la plurarilzacion
        messageSource.setParentMessageSource(icuMessageSource());
        /*
        // definicion de la fuente de mensajes
        messageSource.addBasenames(
                "message/message" // mensajes i18n propios, no se puede cargar sintasis icu en una fuente que se carga en un ResourceBundleMessageSource
                ,"org.hibernate.validator.ValidationMessages" // mensajes i18n de validacion de jakarta
        );

        // configuracion
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.displayName());
        */
        return messageSource;
    }

    /**
     * Documentation:
     * Spring MessageSource ICU Support {@see https://github.com/transferwise/spring-icu}
     * Exmaple {@see https://lokalise.com/blog/spring-boot-internationalization/}
     * @return
     */
    //@Bean // debe ser un Bean, si es el "messageSource es gestionado por spring"
    public ICUMessageSource icuMessageSource() {
        ICUReloadableResourceBundleMessageSource messageSource = new ICUReloadableResourceBundleMessageSource();
        messageSource.setBasenames(
                "message/message",
                //"org.hibernate.validator.ValidationMessages",
                "icu-message/res" // mensajes i18n propios, el "classpath" es requerido
        );
        //messageSource.setUseCodeAsDefaultMessage(true);
        //messageSource.setDefaultEncoding(StandardCharsets.UTF_8.displayName());
        return messageSource;
    }

}
