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

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingFacadeImpl implements RatingFacade{

    @Qualifier("ratingRestClientCredentials")
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    private static final String REGISTER_URL = "/api/ratings/register/films";
    private static final String FIND_RATING_URL = "/api/ratings/films/%d/users/%s";
    private static final String AVERAGE_RATING_URL = "/api/ratings/ratings-average/films/%d";

    @Override
    public RatingFilmDto registerRating(RatingFilmDto ratingFilmDto) {
        try {
            // Lanza la petición POST con JSON
            RatingFilmDto response = restClient.post()
                    .uri(REGISTER_URL)
                    .contentType(MediaType.APPLICATION_JSON)         // JSON en vez de multipart
                    .body(ratingFilmDto)                        // cuerpo = tu DTO
                    .retrieve()
                    .body(RatingFilmDto.class);                      // imperativo con RestClient

            return response;

        } catch (RestClientResponseException e) {
            // Captura 4xx/5xx
            log.error("Error HTTP al guardar el recurso ({}): {}",
                    e.getStatusCode().value(),
                    e.getResponseBodyAsString());
            throw e;

        } catch (Exception e) {
            log.error("Error inesperado al guardar el recurso", e);
            throw new RuntimeException("No pudo registrarse la valoración", e);
        }
    }

    @Override
    public Optional<RatingFilmDto> findRatingByUserIdAndMovieId(String userId, Long movieId) {
        //http://localhost:9095/api/ratings/films/1/users/1
        try {
            // Lanza la petición POST con JSON
            RatingFilmDto response = restClient.get()
                    .uri(FIND_RATING_URL.formatted(movieId,userId))
                    .retrieve()
                    .body(RatingFilmDto.class);                      // imperativo con RestClient

            return Optional.ofNullable(response);

        } catch (RestClientResponseException e) {
            // Captura 4xx/5xx
            log.error("Error HTTP al buscar el recurso ({}): {}",
                    e.getStatusCode().value(),
                    e.getResponseBodyAsString(),e);
            return Optional.empty();

        } catch (Exception e) {
            log.error("Error inesperado al buscar el recurso", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<AverageRating> findRatingAverageByMovieId(Long movieId) {
        try {
            // Lanza la petición POST con JSON
            AverageRating averageRating = restClient.get()
                    .uri(AVERAGE_RATING_URL.formatted(movieId))
                    .retrieve()
                    .body(AverageRating.class);                      // imperativo con RestClient

            return Optional.ofNullable(averageRating);

        } catch (RestClientResponseException e) {
            // Captura 4xx/5xx
            log.error("Error HTTP al buscar el recurso ({}): {}",
                    e.getStatusCode().value(),
                    e.getResponseBodyAsString(),e);
            return Optional.empty();

        } catch (Exception e) {
            log.error("Error inesperado al buscar el recurso", e);
            return Optional.empty();
        }
    }
}
