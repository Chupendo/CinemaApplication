package com.tokioschool.filmapp.repositories;

import com.tokioschool.filmapp.domain.RatingFilm;
import com.tokioschool.filmapp.records.AverageRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad {@link RatingFilm}.
 *
 * Esta interfaz extiende {@link JpaRepository} para proporcionar métodos CRUD
 * y consultas personalizadas para la entidad RatingFilm.
 *
 * Anotaciones:
 * - {@link Repository}: Marca esta interfaz como un componente de acceso a datos de Spring.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Repository
public interface RatingFilmDao extends JpaRepository<RatingFilm, Long> {

    /**
     * Busca una calificación específica de una película realizada por un usuario.
     *
     * @param userId El ID del usuario que realizó la calificación.
     * @param filmId El ID de la película calificada.
     * @return Un {@link Optional} que contiene la calificación si existe.
     */
    Optional<RatingFilm> findRatingFilmByUserIdAndFilmId(String userId, Long filmId);

    /**
     * Calcula el puntaje promedio y el número total de calificaciones para una película específica.
     *
     * @param filmId El ID de la película para la cual se calculará el puntaje promedio.
     * @return Un {@link Optional} que contiene un objeto {@link AverageRating} con el puntaje promedio
     *         y el número total de calificaciones, si existen.
     */
    @Query("SELECT new com.tokioschool.filmapp.records.AverageRating( AVG(r.score), count(*) ) " +
            "FROM RatingFilm r WHERE r.filmId = ?1 GROUP BY r.filmId")
    Optional<AverageRating> mainScoreByFilmId(Long filmId);
}