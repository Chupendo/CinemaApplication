package com.tokioschool.filmweb.helpers;

import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

/**
 * Componente para la obtención de mensajes traducidos desde un archivo de recursos.
 *
 * Esta clase proporciona métodos para recuperar mensajes internacionalizados
 * utilizando claves, parámetros y configuraciones regionales (locales).
 *
 * Anotaciones utilizadas:
 * - `@Component`: Marca esta clase como un componente Spring para la inyección de dependencias.
 * - `@RequiredArgsConstructor`: Genera un constructor con los argumentos requeridos para las dependencias finales.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class TranslatedMessageHelper {

    /** Fuente de mensajes para la internacionalización. */
    private final ResourceBundleMessageSource resourceBundleMessageSource;

    /**
     * Obtiene un mensaje traducido utilizando una clave.
     *
     * @param key Clave del mensaje en el archivo de recursos.
     * @return Mensaje traducido correspondiente a la clave.
     */
    public String getMessage(@NonNull String key) {
        return getMessage(key, LocaleContextHolder.getLocale(), null);
    }

    /**
     * Obtiene un mensaje traducido utilizando una clave y parámetros.
     *
     * @param key Clave del mensaje en el archivo de recursos.
     * @param params Parámetros opcionales para formatear el mensaje.
     * @return Mensaje traducido correspondiente a la clave y parámetros.
     */
    public String getMessage(@NonNull String key, @Nullable Object[] params) {
        return getMessage(key,  LocaleContextHolder.getLocale(), params);
    }

    /**
     * Obtiene un mensaje traducido utilizando una clave, configuración regional y parámetros.
     *
     * @param key Clave del mensaje en el archivo de recursos.
     * @param locale Configuración regional opcional para la traducción. Si es nula, se utiliza la configuración predeterminada.
     * @param params Parámetros opcionales para formatear el mensaje.
     * @return Mensaje traducido correspondiente a la clave, configuración regional y parámetros.
     */
    public String getMessage(@NonNull String key, @Nullable Locale locale, @Nullable Object[] params) {

        final Locale maybeLocale = Optional.ofNullable(locale).orElseGet(Locale::getDefault);
        return resourceBundleMessageSource.getMessage(key, params, StringUtils.EMPTY, maybeLocale);
    }
}