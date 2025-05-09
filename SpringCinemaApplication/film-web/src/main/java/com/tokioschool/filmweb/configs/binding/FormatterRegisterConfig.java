package com.tokioschool.filmweb.configs.binding;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.format.DateTimeFormatter;

/**
 * Configuración para registrar formateadores personalizados en Spring MVC.
 *
 * Esta clase implementa la interfaz `WebMvcConfigurer` para agregar formateadores
 * personalizados que manejan formatos de fecha, hora y fecha-hora en las solicitudes web.
 *
 * Anotaciones utilizadas:
 * - `@Configuration`: Marca esta clase como una clase de configuración de Spring.
 *
 * @author andres.rpenuela
 * @version 1.o
 */
@Configuration
public class FormatterRegisterConfig implements WebMvcConfigurer {

    /**
     * Sobrescribe el método para agregar formateadores al registro.
     *
     * @param registry El registro de formateadores donde se agregan los personalizados.
     */
    @Override
    public void addFormatters(@NonNull FormatterRegistry registry) {
        WebMvcConfigurer.super.addFormatters(registry); // conserva los formateadores existentes

        // Configura y registra los formateadores personalizados
        configDateTimeFormatterRegisterFormatter(registry);
    }

    /**
     * Configura y registra formateadores para tipos de datos de fecha, hora y fecha-hora.
     *
     * @param registry El registro de formateadores donde se agregan los personalizados.
     */
    private static void configDateTimeFormatterRegisterFormatter(FormatterRegistry registry) {
        // Formateadores requeridos para entradas de tipo date, time y datetime-local

        DateTimeFormatterRegistrar register = new DateTimeFormatterRegistrar();
        register.setDateFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd")); // Formato para fechas
        register.setDateTimeFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")); // Formato para fecha-hora
        register.setTimeFormatter(DateTimeFormatter.ofPattern("HH:mm:ss")); // Formato para horas

        // Registra los formateadores en el registro
        register.registerFormatters(registry);
    }
}