package com.tokioschool.ratingapp.controllers;

import com.tokioschool.core.exception.NotFoundException;
import com.tokioschool.core.exception.ValidacionException;
import com.tokioschool.filmapp.dto.ratings.RatingFilmDto;
import com.tokioschool.filmapp.records.AverageRating;
import com.tokioschool.filmapp.records.RatingResponseFilmDto;
import com.tokioschool.filmapp.records.RequestRatingFilmDto;
import com.tokioschool.filmapp.services.ratings.RatingFilmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador para gestionar las operaciones relacionadas con las calificaciones de películas.
 *
 * Este controlador proporciona endpoints para registrar, actualizar, eliminar y consultar calificaciones.
 * También permite calcular el promedio de calificaciones de una película.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@Tag(name = "Ratings API", description = "Operaciones relacionadas con las calificaciones de películas")
public class RatingApiController {

    private final RatingFilmService ratingFilmService;

    /**
     * Recupera todas las calificaciones registradas.
     *
     * @return Lista de calificaciones registradas.
     */
    @Operation(
            summary = "Recuperar calificaciones registradas",
            description = "Obtiene una lista de todas las calificaciones registradas.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de calificaciones recuperada exitosamente",
                            content = @Content(schema = @Schema(implementation = RatingFilmDto.class))
                    )
            }
    )
    @GetMapping("/register/films")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RatingFilmDto>> recoverRatingHandler() {
        return ResponseEntity.ok(ratingFilmService.recoverRatingFilms());
    }

    /**
     * Registra una nueva calificación para una película.
     *
     * @param requestRatingFilmDto Datos de la calificación a registrar.
     * @param bindingResult Resultado de la validación.
     * @return Calificación registrada.
     */
    @Operation(
            summary = "Registrar una calificación",
            description = "Registra una nueva calificación para una película.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Calificación registrada exitosamente",
                            content = @Content(schema = @Schema(implementation = RatingResponseFilmDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Errores de validación",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @PostMapping("/register/films")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RatingResponseFilmDto> registerARatingHandler(@Valid @RequestBody RequestRatingFilmDto requestRatingFilmDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage
                    ));
            throw new ValidacionException("Errores de validación", errors);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingFilmService.save(requestRatingFilmDto));
    }

    /**
     * Actualiza una calificación existente.
     *
     * @param filmId ID de la película.
     * @param userId ID del usuario.
     * @param requestRatingFilmDto Datos actualizados de la calificación.
     * @param bindingResult Resultado de la validación.
     * @return Calificación actualizada.
     */
    @Operation(
            summary = "Actualizar una calificación",
            description = "Actualiza una calificación existente para una película y un usuario.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Calificación actualizada exitosamente",
                            content = @Content(schema = @Schema(implementation = RatingFilmDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Errores de validación",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @PutMapping("/updated/films/{filmId}/users/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RatingFilmDto> updatedARatingHandler(@PathVariable(value = "filmId") Long filmId,
                                                               @PathVariable(value = "userId") String userId,
                                                               @Valid @RequestBody RequestRatingFilmDto requestRatingFilmDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage
                    ));
            throw new ValidacionException("Errores de validación", errors);
        }
        RatingFilmDto ratingFilmDto = ratingFilmService.update(filmId, userId, requestRatingFilmDto);
        return ResponseEntity.ok(ratingFilmDto);
    }

    /**
     * Elimina una calificación existente.
     *
     * @param filmId ID de la película.
     * @param userId ID del usuario.
     * @return Respuesta vacía con código de estado 200.
     */
    @Operation(
            summary = "Eliminar una calificación",
            description = "Elimina una calificación existente para una película y un usuario.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Calificación eliminada exitosamente"
                    )
            }
    )
    @DeleteMapping("/deleted/films/{filmId}/users/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deletedRatingHandler(@PathVariable(value = "filmId") Long filmId,
                                                     @PathVariable(name = "userId") String userId) {
        ratingFilmService.deleteByFilmIdAndUserId(filmId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Busca una calificación específica por película y usuario.
     *
     * @param filmId ID de la película.
     * @param userId ID del usuario.
     * @return Calificación encontrada.
     */
    @Operation(
            summary = "Buscar una calificación",
            description = "Busca una calificación específica por película y usuario.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Calificación encontrada exitosamente",
                            content = @Content(schema = @Schema(implementation = RatingFilmDto.class))
                    )
            }
    )
    @GetMapping("/films/{filmId}/users/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RatingFilmDto> findRatingByFilmAndUserHandler(@PathVariable(name = "filmId") Long filmId,
                                                                        @PathVariable(name = "userId") String userId) {
        RatingFilmDto requestRatingFilmDto = ratingFilmService.findRatingByFilmAndUserHandler(filmId, userId);
        return ResponseEntity.ok(requestRatingFilmDto);
    }

    /**
     * Calcula el promedio de calificaciones de una película.
     *
     * @param filmId ID de la película.
     * @return Promedio de calificaciones.
     * @throws NotFoundException Si no se encuentran calificaciones para la película.
     */
    @Operation(
            summary = "Calcular promedio de calificaciones",
            description = "Calcula el promedio de calificaciones de una película específica.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Promedio de calificaciones calculado exitosamente",
                            content = @Content(schema = @Schema(implementation = AverageRating.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No se encontraron calificaciones para la película",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @GetMapping("/ratings-average/films/{filmId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AverageRating> averageRatingsHandler(@PathVariable(name = "filmId") Long filmId) throws NotFoundException {
        return ResponseEntity.ok(ratingFilmService.averageRatings(filmId));
    }
}