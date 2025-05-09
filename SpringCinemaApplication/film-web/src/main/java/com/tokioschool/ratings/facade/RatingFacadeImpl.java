package com.tokioschool.ratings.facade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokioschool.filmapp.dto.ratings.RatingFilmDto;
import com.tokioschool.filmapp.records.AverageRating;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Optional;

/**
 * Implementación de la interfaz `RatingFacade` que proporciona las operaciones relacionadas
 * con las calificaciones de películas.
 *
 * Esta clase utiliza un cliente REST para interactuar con un servicio externo y realizar
 * operaciones como registrar calificaciones, buscar calificaciones específicas y calcular
 * el promedio de calificaciones.
 *
 * Anotaciones utilizadas:
 * - `@Service`: Marca esta clase como un componente de servicio de Spring.
 * - `@RequiredArgsConstructor`: Genera un constructor con los argumentos requeridos
 *   para las dependencias inyectadas.
 * - `@Slf4j`: Proporciona un logger para registrar mensajes de depuración y errores.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RatingFacadeImpl implements RatingFacade {

    /** Cliente REST configurado para realizar solicitudes autenticadas. */
    @Qualifier("ratingRestClientCredentials")
    private final RestClient restClient;

    /** Objeto para mapear datos JSON. */
    private final ObjectMapper objectMapper;

    /** URL para registrar una nueva calificación. */
    private static final String REGISTER_URL = "/api/ratings/register/films";

    /** URL para buscar una calificación específica por usuario y película. */
    private static final String FIND_RATING_URL = "/api/ratings/films/%d/users/%s";

    /** URL para calcular el promedio de calificaciones de una película. */
    private static final String AVERAGE_RATING_URL = "/api/ratings/ratings-average/films/%d";

    /**
     * Registra una nueva calificación para una película.
     *
     * @param ratingFilmDto Objeto que contiene los datos de la calificación a registrar.
     * @return Un objeto `RatingFilmDto` que representa la calificación registrada.
     * @throws RestClientResponseException Si ocurre un error HTTP durante la solicitud.
     * @throws RuntimeException Si ocurre un error inesperado.
     */
    @Override
    public RatingFilmDto registerRating(RatingFilmDto ratingFilmDto) {
        try {
            RatingFilmDto response = restClient.post()
                    .uri(REGISTER_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ratingFilmDto)
                    .retrieve()
                    .body(RatingFilmDto.class);

            return response;

        } catch (RestClientResponseException e) {
            log.error("Error HTTP al guardar el recurso ({}): {}",
                    e.getStatusCode().value(),
                    e.getResponseBodyAsString());
            throw e;

        } catch (Exception e) {
            log.error("Error inesperado al guardar el recurso", e);
            throw new RuntimeException("No pudo registrarse la valoración", e);
        }
    }

    /**
     * Busca una calificación específica basada en el ID del usuario y el ID de la película.
     *
     * @param userId El ID del usuario que realizó la calificación.
     * @param movieId El ID de la película calificada.
     * @return Un `Optional` que contiene el objeto `RatingFilmDto` si se encuentra la calificación,
     *         o vacío si no existe.
     */
    @Override
    public Optional<RatingFilmDto> findRatingByUserIdAndMovieId(String userId, Long movieId) {
        try {
            RatingFilmDto response = restClient.get()
                    .uri(FIND_RATING_URL.formatted(movieId, userId))
                    .retrieve()
                    .body(RatingFilmDto.class);

            return Optional.ofNullable(response);

        } catch (RestClientResponseException e) {
            log.error("Error HTTP al buscar el recurso ({}): {}",
                    e.getStatusCode().value(),
                    e.getResponseBodyAsString(), e);
            return Optional.empty();

        } catch (Exception e) {
            log.error("Error inesperado al buscar el recurso", e);
            return Optional.empty();
        }
    }

    /**
     * Calcula el promedio de calificaciones para una película específica.
     *
     * @param movieId El ID de la película para la cual se calculará el promedio de calificaciones.
     * @return Un `Optional` que contiene un objeto `AverageRating` con el promedio de calificaciones,
     *         o vacío si no hay calificaciones disponibles.
     */
    @Override
    public Optional<AverageRating> findRatingAverageByMovieId(Long movieId) {
        try {
            AverageRating averageRating = restClient.get()
                    .uri(AVERAGE_RATING_URL.formatted(movieId))
                    .retrieve()
                    .body(AverageRating.class);

            return Optional.ofNullable(averageRating);

        } catch (RestClientResponseException e) {
            log.error("Error HTTP al buscar el recurso ({}): {}",
                    e.getStatusCode().value(),
                    e.getResponseBodyAsString(), e);
            return Optional.empty();

        } catch (Exception e) {
            log.error("Error inesperado al buscar el recurso", e);
            return Optional.empty();
        }
    }
}