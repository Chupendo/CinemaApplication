package com.tokioschool.filmapp.controller;

import com.tokioschool.core.exception.NotFoundException;
import com.tokioschool.core.exception.ValidacionException;
import com.tokioschool.filmapp.controller.schemas.MovieFormRequestSchema;
import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.movie.FilterMovie;
import com.tokioschool.filmapp.dto.movie.MovieDto;
import com.tokioschool.filmapp.records.RangeReleaseYear;
import com.tokioschool.filmapp.records.SearchMovieRecord;
import com.tokioschool.filmapp.services.movie.MovieService;
import com.tokioschool.helpers.UUIDHelper;
import com.tokioschool.store.dto.ResourceIdDto;
import com.tokioschool.store.facade.StoreFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestionar operaciones relacionadas con películas.
 *
 * Este controlador proporciona endpoints para buscar, registrar, actualizar y obtener detalles de películas.
 *
 * Anotaciones:
 * - {@link RestController}: Indica que esta clase es un controlador REST.
 * - {@link RequestMapping}: Define la ruta base para los endpoints de este controlador.
 * - {@link Tag}: Proporciona metadatos para la documentación de Swagger.
 *
 * Dependencias:
 * - {@link MovieService}: Servicio para manejar la lógica de negocio relacionada con películas.
 * - {@link StoreFacade}: Facade para gestionar recursos almacenados, como imágenes.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/film/api/movies")
@Tag(name = "movies", description = "movies operations")
@Slf4j
public class MovieApiController {

    private final MovieService movieService;
    private final StoreFacade storeFacade;


    /**
     * Endpoint para buscar películas por título y/o rango de años de lanzamiento.
     *
     * @param page Número de página para la paginación (por defecto: 0).
     * @param pageSize Número de registros por página (por defecto: 10).
     * @param filterMovie Objeto {@link FilterMovie} con los filtros de búsqueda.
     * @return Una respuesta HTTP con los resultados de la búsqueda y un código de estado 200 (OK).
     */
    @Operation(
            summary = "Buscar películas por título o rango de años",
            description = "Permite buscar películas proporcionando un título y/o un rango de años de lanzamiento. También se pueden definir parámetros de paginación.",
            parameters = {
                    @Parameter(name = "page", description = "Número de página para la paginación (por defecto: 0)", required = false, example = "1"),
                    @Parameter(name = "pageSize", description = "Número de registros por página (por defecto: 10)", required = false, example = "10"),
                    @Parameter(name = "filter", description = "Objeto de filtro con título y rango de años", required = false, content = @Content(mediaType = "application/json", schema = @Schema(implementation = FilterMovie.class)))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resultados de búsqueda obtenidos exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json"))
            }
    )
    @SecurityRequirement(name = "auth-openapi")
    @GetMapping(value = {"", "/", "/search"}, consumes = {MediaType.ALL_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageDTO<MovieDto>> searchMoviesHandler(
            @RequestParam(value = "page",required = false, defaultValue = "0") int page,
            @RequestParam(value = "pageSize",required = false, defaultValue = "0") int pageSize,
            @RequestPart(value = "filter",required = false) FilterMovie filterMovie
    ){

        filterMovie = Optional.ofNullable(filterMovie).orElse(new FilterMovie());

        SearchMovieRecord searchMovieRecord = SearchMovieRecord.builder()
                .title(filterMovie.getTitle())
                .rangeReleaseYear(new RangeReleaseYear(
                        Optional.ofNullable(filterMovie.getYearMin()).orElse(0), // Default yearMin
                        Optional.ofNullable(filterMovie.getYearMax()).orElse(0)  // Default yearMax
                ))
                .page(page)
                .pageSize(pageSize)
                .build();

        return ResponseEntity.ok( movieService.searchMovie(searchMovieRecord) );
    }

    /**
     * Endpoint para obtener los detalles de una película por su ID.
     *
     * @param movieId ID de la película en el sistema.
     * @return Una respuesta HTTP con los detalles de la película y un código de estado 200 (OK).
     */
    @Operation(
            summary = "Obtener detalles de una película por ID",
            description = "Devuelve los detalles de una película específica utilizando su ID.",
            parameters = {
                    @Parameter(name = "id", description = "ID de la película en el sistema", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Detalles de la película obtenidos exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovieDto.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json"))
            }
    )
    @SecurityRequirement(name = "auth-openapi")
    @GetMapping(value = {"/{id}", "/view/{id}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MovieDto> getMovieByIdHandler(
            @PathVariable(value = "id") Long movieId){
        try {
            return ResponseEntity.ok(movieService.getMovieById(movieId));
        }catch (InvalidDataAccessApiUsageException danseuse){
            log.error(danseuse.getMessage(),danseuse);
            throw new NotFoundException("Movie don't found, because %s".formatted(danseuse.getMessage()),danseuse);
        }
    }

    /**
     * Endpoint para registrar una nueva película.
     *
     * @param multipartFile Archivo de imagen de la película.
     * @param description Descripción de la película.
     * @param movieDto Objeto {@link MovieDto} con los datos de la película.
     * @param bindingResult Resultado de la validación de los datos.
     * @return Una respuesta HTTP con la película registrada y un código de estado 200 (OK).
     */
    @Operation(
            summary = "Registrar una nueva película",
            description = "Permite registrar una película con imagen, descripción y datos generales. Requiere autenticación.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(implementation = MovieFormRequestSchema.class, contentMediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            ),
            method = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Película registrada exitosamente", content = @Content(schema = @Schema(implementation = MovieDto.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta o datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "415", description = "Tipo de contenido no soportado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "auth-openapi")
    public ResponseEntity<MovieDto> createMovieHandler(
            @RequestPart(value = "image") MultipartFile multipartFile,
            @RequestPart(value = "description", required = false) String description,
            @Valid @RequestPart(value = "movieFormDto") MovieDto movieDto, BindingResult bindingResult) {

        if(multipartFile == null || multipartFile.isEmpty()){
            Map<String, String> errors = Collections.singletonMap("image","Image is required");
            throw new ValidacionException("Errores de validación", errors);
        }

        if(bindingResult.hasErrors()){
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage
                    ));
            throw new ValidacionException("Errores de validación", errors);
        }

        // save image and setting to image
        Optional<ResourceIdDto> resourceIdDtoOptional = storeFacade.saveResource(multipartFile,description);
        resourceIdDtoOptional.ifPresent(resourceIdDto -> {
            UUIDHelper.mapStringToUUID(  movieDto.getResourceId() ).ifPresent(storeFacade::deleteResource);
            movieDto.setResourceId( resourceIdDto.resourceId().toString() );
        });

        // create movie
        return ResponseEntity.ok(movieService.createMovie(movieDto));
    }

    /**
     * Endpoint para actualizar una película existente.
     *
     * @param movieId ID de la película a actualizar.
     * @param multipartFile Archivo de imagen de la película.
     * @param description Descripción de la película.
     * @param movieDto Objeto {@link MovieDto} con los datos actualizados de la película.
     * @param bindingResult Resultado de la validación de los datos.
     * @return Una respuesta HTTP con la película actualizada y un código de estado 200 (OK).
     */
    @Operation(
            summary = "Actualizar una película existente",
            description = "Permite actualizar una película con imagen, descripción y datos generales. Requiere autenticación.",
            parameters = {
                    @Parameter(name = "movieId", description = "ID de la película en el sistema", required = true, example = "1")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(implementation = MovieFormRequestSchema.class, contentMediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            ),
            method = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Película actualizada exitosamente", content = @Content(schema = @Schema(implementation = MovieDto.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta o datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "415", description = "Tipo de contenido no soportado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "auth-openapi")
    @PutMapping(value = "/updated/{movieId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieDto> updatedMovieHandler(
            @PathVariable(value="movieId") Long movieId,
            @RequestPart(value = "image", required = false) MultipartFile multipartFile,
            @RequestPart(value = "description", required = false) String description,
            @Valid @RequestPart(value = "movieFormDto") MovieDto movieDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            Map<String, String> errores = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage
                    ));
            throw new ValidacionException("Errores de validación", errores);
        }

        if( multipartFile != null && !multipartFile.isEmpty() ){
            Optional<ResourceIdDto> resourceIdDtoOptional = storeFacade.saveResource(multipartFile,description);
            resourceIdDtoOptional.ifPresent(resourceIdDto -> {
                UUIDHelper.mapStringToUUID(  movieDto.getResourceId() ).ifPresent(storeFacade::deleteResource);
                movieDto.setResourceId( resourceIdDto.resourceId().toString() );
            });
        }

        return ResponseEntity.ok(movieService.updateMovie(movieId,movieDto));
    }
}
