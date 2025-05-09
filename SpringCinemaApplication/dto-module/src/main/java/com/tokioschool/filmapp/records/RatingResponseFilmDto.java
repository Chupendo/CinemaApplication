package com.tokioschool.filmapp.records;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Registro que representa la respuesta de calificación de una película.
 *
 * Este registro encapsula la información relacionada con la calificación de una película,
 * incluyendo la puntuación y la fecha/hora asociada.
 *
 * Es inmutable y se utiliza para transferir datos de manera eficiente.
 *
 * @param score          Puntuación de la película.
 * @param localDateTime  Fecha y hora en que se realizó la calificación, con formato "yyyy-MM-dd'T'HH:mm".
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public record RatingResponseFilmDto(BigDecimal score,
                                    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
                                    LocalDateTime localDateTime) {
}