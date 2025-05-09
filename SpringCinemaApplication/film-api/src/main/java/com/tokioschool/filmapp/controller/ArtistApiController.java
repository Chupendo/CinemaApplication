package com.tokioschool.filmapp.controller;

import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.core.exception.ValidacionException;
import com.tokioschool.filmapp.services.artist.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestionar operaciones relacionadas con artistas.
 *
 * Este controlador proporciona endpoints para registrar artistas y obtener
 * una lista de todos los artistas registrados en el sistema.
 *
 * Anotaciones:
 * - {@link RestController}: Indica que esta clase es un controlador REST.
 * - {@link RequestMapping}: Define la ruta base para los endpoints de este controlador.
 * - {@link Tag}: Proporciona metadatos para la documentación de Swagger.
 *
 * Dependencias:
 * - {@link ArtistService}: Servicio para manejar la lógica de negocio relacionada con artistas.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/film/api/artists")
@Tag(name = "artist", description = "Operaciones relacionadas con artistas")
public class ArtistApiController {

    private final ArtistService artistService;

    /**
     * Endpoint para registrar un nuevo artista en el sistema.
     *
     * Este metodo valida los datos del artista proporcionados en el cuerpo de la solicitud
     * y los registra en el sistema si son válidos.
     *
     * @param artistDto Objeto {@link ArtistDto} que contiene los datos del artista a registrar.
     * @param bindingResult Resultado de la validación de los datos del artista.
     * @return Una respuesta HTTP con el artista registrado y un código de estado 201 (CREATED).
     * @throws ValidacionException Si hay errores de validación en los datos del artista.
     */
    @Operation(
            summary = "Registrar un artista en el sistema",
            description = "Este endpoint permite registrar un nuevo artista en el sistema.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Artista registrado exitosamente",
                            content = @Content(schema = @Schema(implementation = ArtistDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Errores de validación en los datos del artista",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autorizado",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            },
            security = @SecurityRequirement(name = "auth-openapi")
    )
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "auth-openapi")
    public ResponseEntity<ArtistDto> registerArtistDto(@Valid @RequestBody ArtistDto artistDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errores = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage
                    ));
            throw new ValidacionException("Errores de validación", errores);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(artistService.registerArtist(artistDto));
    }

    /**
     * Endpoint para obtener una lista de todos los artistas registrados.
     *
     * Este metodo devuelve una lista de objetos {@link ArtistDto} que representan
     * a todos los artistas registrados en el sistema.
     *
     * @return Una respuesta HTTP con la lista de artistas y un código de estado 200 (OK).
     */
    @Operation(
            summary = "Obtener todos los artistas",
            description = "Este endpoint devuelve una lista de todos los artistas registrados en el sistema.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de artistas obtenida exitosamente",
                            content = @Content(schema = @Schema(implementation = ArtistDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autorizado",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            },
            security = @SecurityRequirement(name = "auth-openapi")
    )
    @GetMapping(value = "/find-all", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "auth-openapi")
    public ResponseEntity<List<ArtistDto>> findAllArtists() {
        return ResponseEntity.ok(artistService.findByAll());
    }
}