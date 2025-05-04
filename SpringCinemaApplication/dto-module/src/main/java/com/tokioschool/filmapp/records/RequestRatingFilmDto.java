package com.tokioschool.filmapp.records;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * Registro que representa una solicitud de calificación para una película.
 *
 * Este registro encapsula la información necesaria para realizar una solicitud
 * de calificación de una película, incluyendo el identificador de la solicitud,
 * el usuario, la película y la puntuación asignada.
 *
 * Es inmutable y se utiliza para transferir datos de manera eficiente.
 *
 * @param id      Identificador único de la solicitud.
 * @param userId  Identificador del usuario que realiza la calificación. Debe ser un valor positivo y no nulo.
 * @param filmId  Identificador de la película a calificar. Debe ser un valor positivo y no nulo.
 * @param score   Puntuación asignada a la película. Debe ser un valor entre 0 y 100, positivo o cero, y no nulo.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public record RequestRatingFilmDto(Long id,
                                   @NotNull @Positive String userId,
                                   @NotNull @Positive Long filmId,
                                   @NotNull @PositiveOrZero @Max(100) BigDecimal score) {
}