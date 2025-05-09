package com.tokioschool.filmweb.configs.internazionalitation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

/**
 * Configuración para la internacionalización de la aplicación.
 *
 * Esta clase configura un interceptor para cambiar el idioma de la aplicación
 * basado en un parámetro de la URL y define un resolutor de locales basado en cookies.
 *
 * Anotaciones utilizadas:
 * - `@Configuration`: Marca esta clase como una clase de configuración de Spring.
 * - `@Slf4j`: Habilita el registro de logs utilizando Lombok.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
@Slf4j
public class LocaleInterceptorConfig implements WebMvcConfigurer {

    /** Nombre del parámetro utilizado por el interceptor para cambiar el idioma. */
    public static final String INTERCEPTOR_PARAM_NAME = "lang";

    /** Idioma predeterminado configurado en las propiedades de la aplicación. */
    @Value("${spring.web.locale : en}")
    private String defaultLocale;

    /**
     * Define un bean para el resolutor de locales basado en cookies.
     *
     * Este resolutor utiliza cookies para almacenar el idioma seleccionado por el usuario.
     *
     * @return Una instancia de `CookieLocaleResolver` con el idioma predeterminado configurado.
     */
    @Bean
    public LocaleResolver localeResolver() {
        log.info("LocaleInterceptorConfig.localeResolver() -> defaultLocale: {}", defaultLocale);
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setDefaultLocale(Locale.of(defaultLocale));
        return localeResolver;
    }

    /**
     * Define un bean para el interceptor de cambio de idioma.
     *
     * Este interceptor lee el parámetro "lang" en la URL de cualquier controlador
     * y actualiza el idioma de la aplicación en consecuencia.
     *
     * @return Una instancia de `LocaleChangeInterceptor`.
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        log.info("LocaleInterceptorConfig.localeChangeInterceptor() -> interceptor param name: {}", INTERCEPTOR_PARAM_NAME);
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName(INTERCEPTOR_PARAM_NAME);
        return localeChangeInterceptor;
    }

    /**
     * Registra el interceptor de cambio de idioma en la lista de interceptores de la aplicación.
     *
     * @param registry El registro de interceptores donde se agrega el interceptor personalizado.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}