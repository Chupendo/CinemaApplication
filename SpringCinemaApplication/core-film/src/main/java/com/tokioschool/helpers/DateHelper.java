package com.tokioschool.helpers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

/**
 * Clase auxiliar para trabajar con fechas y horas.
 *
 * Proporciona métodos estáticos para realizar conversiones entre diferentes tipos
 * de objetos relacionados con fechas y horas.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class DateHelper {

    /**
     * Constructor privado para evitar la instanciación de la clase.
     */
    private DateHelper() {
        // Prevent instantiation
    }

    /**
     * Convierte un objeto de tipo \{@link LocalDateTime\} a un \{@link OffsetDateTime\}
     * con la zona horaria UTC+0.
     *
     * @param localDateTime el objeto \{@link LocalDateTime\} a convertir
     * @return el objeto \{@link OffsetDateTime\} convertido, o null si la entrada es null
     */
    public static OffsetDateTime parseLocalDateToTimeToOffsetDateTimeUtc(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime)
                .map(time -> time.atOffset(ZoneOffset.UTC))
                .orElseGet(() -> null);
    }
}