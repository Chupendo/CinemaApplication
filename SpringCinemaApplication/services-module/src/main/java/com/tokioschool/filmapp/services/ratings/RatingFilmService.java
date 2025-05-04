package com.tokioschool.filmapp.services.ratings;

import com.tokioschool.core.exception.NotFoundException;
import com.tokioschool.filmapp.dto.ratings.RatingFilmDto;
import com.tokioschool.filmapp.records.AverageRating;
import com.tokioschool.filmapp.records.RatingResponseFilmDto;
import com.tokioschool.filmapp.records.RequestRatingFilmDto;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * Interfaz para el servicio de gestión de calificaciones de películas.
 *
 * Esta interfaz define los métodos necesarios para realizar operaciones relacionadas
 * con la entidad RatingFilm, como recuperar calificaciones, guardar una nueva,
 * actualizar una existente, eliminar y calcular el promedio de calificaciones.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public interface RatingFilmService {

    /**
     * Recupera todas las calificaciones de películas.
     *
     * @return Una lista de objetos {@link RatingFilmDto} que representan las calificaciones.
     */
    List<RatingFilmDto> recoverRatingFilms();

    /**
     * Guarda una nueva calificación de película.
     *
     * @param requestRatingFilmDto Datos de la calificación a guardar.
     * @return Un objeto {@link RatingResponseFilmDto} con los detalles de la calificación guardada.
     * @throws IncorrectResultSizeDataAccessException Si ocurre un error al acceder a los datos.
     */
    RatingResponseFilmDto save(@NonNull RequestRatingFilmDto requestRatingFilmDto) throws IncorrectResultSizeDataAccessException;

    /**
     * Actualiza una calificación existente.
     *
     * @param filmId ID de la película.
     * @param userId ID del usuario.
     * @param requestRatingFilmDto Datos de la calificación a actualizar.
     * @return Un objeto {@link RatingFilmDto} con los detalles de la calificación actualizada.
     * @throws IncorrectResultSizeDataAccessException Si ocurre un error al acceder a los datos.
     */
    RatingFilmDto update(@NonNull Long filmId, @NonNull String userId, @NonNull RequestRatingFilmDto requestRatingFilmDto) throws IncorrectResultSizeDataAccessException;

    /**
     * Encuentra una calificación de película por el ID de la película y el ID del usuario.
     *
     * @param filmId ID de la película.
     * @param userId ID del usuario.
     * @return Un objeto {@link RatingFilmDto} con los detalles de la calificación encontrada.
     * @throws IncorrectResultSizeDataAccessException Si ocurre un error al acceder a los datos.
     */
    RatingFilmDto findRatingByFilmAndUserHandler(@NonNull Long filmId, @NonNull String userId) throws IncorrectResultSizeDataAccessException;

    /**
     * Elimina una calificación de película por el ID de la película y el ID del usuario.
     *
     * @param filmId ID de la película.
     * @param userId ID del usuario.
     * @throws IncorrectResultSizeDataAccessException Si ocurre un error al acceder a los datos.
     */
    void deleteByFilmIdAndUserId(@NonNull Long filmId, @NonNull String userId) throws IncorrectResultSizeDataAccessException;

    /**
     * Calcula el promedio de calificaciones para una película.
     *
     * @param filmId ID de la película.
     * @return Un objeto {@link AverageRating} con el promedio de calificaciones.
     * @throws NotFoundException Si no hay calificaciones para la película.
     */
    AverageRating averageRatings(Long filmId) throws NotFoundException;
}