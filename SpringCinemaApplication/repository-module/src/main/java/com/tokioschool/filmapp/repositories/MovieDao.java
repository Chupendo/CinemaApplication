package com.tokioschool.filmapp.repositories;

import com.tokioschool.filmapp.domain.Movie;
import com.tokioschool.filmapp.projections.ResultMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad {@link Movie}.
 *
 * Esta interfaz extiende {@link JpaRepository} para proporcionar métodos CRUD
 * y consultas personalizadas para la entidad Movie.
 *
 * Anotaciones:
 * - {@link Repository}: Marca esta interfaz como un componente de acceso a datos de Spring.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Repository
public interface MovieDao extends JpaRepository<Movie, Long> {

        /**
         * Busca todas las películas cuyo título contenga la palabra especificada,
         * ignorando mayúsculas y minúsculas.
         *
         * @param title El texto que debe estar contenido en el título de las películas.
         * @return Una lista de proyecciones {@link ResultMovie} que coinciden con el criterio.
         */
        List<ResultMovie> findMovieByTitleContainsIgnoreCase(String title);

        /**
         * Busca películas cuyo año de lanzamiento esté dentro de un intervalo inclusivo.
         *
         * @param yearMin El año mínimo del intervalo.
         * @param yearMax El año máximo del intervalo.
         * @return Una lista de proyecciones {@link ResultMovie} que coinciden con el criterio.
         */
        List<ResultMovie> findMovieByReleaseYearBetween(Integer yearMin, Integer yearMax);

        /**
         * Busca películas cuyo año de lanzamiento sea igual al especificado.
         *
         * @param year El año de lanzamiento de las películas a buscar.
         * @return Una lista de proyecciones {@link ResultMovie} que coinciden con el criterio.
         */
        List<ResultMovie> findMovieByReleaseYearIs(Integer year);


        /**
         * Encuentra las películas que no han sido exportadas.
         *
         * Esta consulta selecciona todas las películas cuyo ID no está presente
         * en la tabla `ExportFilms`.
         *
         * @return Lista de películas no exportadas.
         */
        @Query("SELECT m FROM Movie m WHERE m.id NOT IN (SELECT ef.movie.id FROM ExportFilm ef)")
        // @Query("SELECT f FROM Film f LEFT JOIN ExportFilm ef ON ef.film = f WHERE ef.film IS NULL")
        List<Movie> findFilmsNotExported();

        List<Movie> findMovieByManagerId(Long id);
}