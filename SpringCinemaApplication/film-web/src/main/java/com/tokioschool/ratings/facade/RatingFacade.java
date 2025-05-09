package com.tokioschool.ratings.facade;

import com.tokioschool.filmapp.dto.ratings.RatingFilmDto;
import com.tokioschool.filmapp.records.AverageRating;

import java.util.Optional;

/**
 * Interfaz que define las operaciones relacionadas con las calificaciones de películas.
 *
 * Esta interfaz proporciona métodos para registrar calificaciones, buscar calificaciones
 * específicas y calcular el promedio de calificaciones de una película.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public interface RatingFacade {

    /**
     * Registra una nueva calificación para una película.
     *
     * @param ratingFilmDto Objeto que contiene los datos de la calificación a registrar.
     * @return Un objeto `RatingFilmDto` que representa la calificación registrada.
     */
    RatingFilmDto registerRating(RatingFilmDto ratingFilmDto);

    /**
     * Busca una calificación específica basada en el ID del usuario y el ID de la película.
     *
     * @param userId El ID del usuario que realizó la calificación.
     * @param movieId El ID de la película calificada.
     * @return Un `Optional` que contiene el objeto `RatingFilmDto` si se encuentra la calificación,
     *         o vacío si no existe.
     */
    Optional<RatingFilmDto> findRatingByUserIdAndMovieId(String userId, Long movieId);

    /**
     * Calcula el promedio de calificaciones para una película específica.
     *
     * @param movieId El ID de la película para la cual se calculará el promedio de calificaciones.
     * @return Un `Optional` que contiene un objeto `AverageRating` con el promedio de calificaciones,
     *         o vacío si no hay calificaciones disponibles.
     */
    Optional<AverageRating> findRatingAverageByMovieId(Long movieId);
}