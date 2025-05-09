package com.tokioschool.filmapp.dto.ratings;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Clase DTO (Data Transfer Object) para representar la calificación de una película.
 *
 * Esta clase se utiliza para transferir datos relacionados con la calificación
 * de una película, incluyendo el identificador del usuario, el identificador de la película,
 * la puntuación y las marcas de tiempo de creación y actualización.
 * Utiliza anotaciones de Jackson y Spring para el manejo de formatos de fecha y hora.
 *
 * Es mutable y utiliza las anotaciones de Lombok para generar automáticamente
 * los métodos getter, setter, constructor, etc.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingFilmDto {

    /**
     * Identificador único de la calificación.
     */
    private Long id;

    /**
     * Identificador del usuario que realizó la calificación.
     */
    private String userId;

    /**
     * Identificador de la película calificada.
     */
    private Long filmId;

    /**
     * Puntuación asignada a la película.
     * Representada como un valor decimal.
     */
    @NotNull @Min(0) @Max(5)
    private BigDecimal score;

    /**
     * Fecha y hora de creación de la calificación.
     * Formateada según el estándar ISO 8601.
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) // Interpretar valores que vienen como texto en una URL o formulario.
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mmZ") // Serializar y deserializar el objeto a un formato de fecha y hora específico.
    private OffsetDateTime createAt;

    /**
     * Fecha y hora de la última actualización de la calificación.
     * Formateada según el estándar ISO 8601.
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mmZ")
    private OffsetDateTime updatedAt;
}