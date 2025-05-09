package com.tokioschool.filmapp.records;

import lombok.Builder;

/**
 * Registro que representa la calificación promedio de una película.
 *
 * Este registro encapsula la información relacionada con la calificación promedio
 * de una película, incluyendo el valor promedio y el número total de calificaciones.
 *
 * Es inmutable y se utiliza para transferir datos de manera eficiente.
 *
 * @param average Valor promedio de las calificaciones.
 * @param ratings Número total de calificaciones recibidas.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Builder
public record AverageRating(Double average, Long ratings) {
}