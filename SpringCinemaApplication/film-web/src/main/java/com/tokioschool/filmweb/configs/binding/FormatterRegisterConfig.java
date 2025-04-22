package com.tokioschool.filmweb.configs.binding;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.format.DateTimeFormatter;

@Configuration
public class FormatterRegisterConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(@NonNull FormatterRegistry registry) {
        WebMvcConfigurer.super.addFormatters(registry); // conserva lo anterior

        configDateTimeFormatterRegisterFormatter(registry);
    }

    private static void configDateTimeFormatterRegisterFormatter(FormatterRegistry registry) {
        // formater requeried por the inputs de tipo date, time y datetime-local

        DateTimeFormatterRegistrar register = new DateTimeFormatterRegistrar();
        register.setDateFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        register.setDateTimeFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        register.setTimeFormatter(DateTimeFormatter.ofPattern("HH:mm:ss"));

        register.registerFormatters(registry);
    }
}
