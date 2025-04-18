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

@Configuration
@Slf4j
public class LocaleInterceptorConfig implements WebMvcConfigurer {

    public static final String INTERCEPTOR_PARAM_NAME = "lang";
    @Value("${spring.web.locale : en}")
     private String defaultLocale;

    /**
     * Created a Bean to LocalResolver based to Cookie and not of HTTP-HEADER
     *
     * @return instance of cookie locale resolver with default locale english
     */
    @Bean
    public LocaleResolver localeResolver() {
        log.info("LocaleInterceptorConfig.localeResolver() -> defaultLocale: {}", defaultLocale);
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setDefaultLocale( Locale.of( defaultLocale ) );
        return localeResolver;
    }

    /**
     * Created a Bean to locale interceptor, this read in the url to any controller the param "lang"
     * and updated the locale
     *
     * @return instance a locale change interceptor
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        log.info("LocaleInterceptorConfig.localeChangeInterceptor() -> interceptor param name: {}", INTERCEPTOR_PARAM_NAME);
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName(INTERCEPTOR_PARAM_NAME);
        return localeChangeInterceptor;
    }

    /**
     * Registrer to list of interceptors, own interceptor
     *
     * @param registry injection of dependency of registry of interceptors of application
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry ) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
