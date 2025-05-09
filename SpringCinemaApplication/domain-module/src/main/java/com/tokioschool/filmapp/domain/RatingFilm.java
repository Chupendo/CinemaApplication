package com.tokioschool.filmapp.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa una calificación de una película en la aplicación.
 *
 * Esta clase define la entidad `RatingFilm` que se mapea a la tabla `film_ratings` en la base de datos.
 * Incluye información como el puntaje, el identificador de la película, el usuario que realizó la calificación,
 * y las marcas de tiempo de creación y actualización.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "film_ratings") // Define el nombre de la tabla en la base de datos
public class RatingFilm {

    /**
     * Identificador único de la calificación.
     * Se genera automáticamente utilizando la estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Puntaje de la calificación.
     * Este campo no puede ser nulo.
     */
    @Column(nullable = false)
    private BigDecimal score;

    /**
     * Identificador de la película asociada a la calificación.
     * Este campo no puede ser nulo.
     */
    @Column(name = "film_id", nullable = false)
    private Long filmId;

    /**
     * Identificador del usuario que realizó la calificación.
     * Este campo no puede ser nulo.
     */
    @Column(name = "user_id", nullable = false)
    private String userId;

    /**
     * Marca de tiempo de creación de la calificación.
     * Se establece automáticamente al momento de la creación.
     */
    @Column(name = "create_at")
    @CurrentTimestamp
    private LocalDateTime createAt;

    /**
     * Marca de tiempo de la última actualización de la calificación.
     */
    @Column(name = "updated_at")
    private LocalDateTime updateAt;
}