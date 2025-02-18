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

@RestController
@RequiredArgsConstructor
@RequestMapping("/film/api/movies")
@Tag(name="movies", description= "movies operations")
@Slf4j
public class MovieApiController {

    private final MovieService movieService;
    private final StoreFacade storeFacade;


    @Operation(
            summary = "Get search by filter title or/and range release year",
            description = "Search for movies by providing a title and/or a range of release years. You can also define pagination parameters. If \"pageSize\" is 0, then return all movies filters in a page.",
            parameters = {
                    @Parameter(
                            name = "page",
                            description = "Page number for pagination (default: 0)",
                            required = false,
                            example = "1"
                    ),
                    @Parameter(
                            name = "pageSize",
                            description = "Number of records per page (default: 10)",
                            required = false,
                            example = "10"
                    ),
                    @Parameter(
                            name = "filter",
                            description = "Filter object containing title and release year range",
                            required = false,
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = FilterMovie.class))
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval of movie search results",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PageDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request (e.g., malformed filter)",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized access (user not authenticated)",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @SecurityRequirement(name = "auth-openapi")
    @GetMapping(value={"","/","/search"},consumes = {MediaType.ALL_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
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

    @Operation(
            summary = "Get search by filter title or/and range release year",
            description = "Search for movies by providing a title and/or a range of release years. You can also define pagination parameters. If \"pageSize\" is 0, then return all movies filters in a page.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Identification movie in the system (default: 0)",
                            required = true,
                            example = "1"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval of movie search results",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PageDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request (e.g., malformed filter)",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized access (user not authenticated)",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @SecurityRequirement(name = "auth-openapi")
    @GetMapping(value={"/{id}","/view/{id}",}, produces = MediaType.APPLICATION_JSON_VALUE)
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

    @SecurityRequirement(name = "auth-openapi")
    @Operation(
            summary = "Registrar una nueva película",
            description = "Este endpoint permite registrar una película con imagen, descripción y datos generales. Requiere autenticación.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(implementation = MovieFormRequestSchema.class,contentMediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
                    )
            ),
            method = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Película registrada exitosamente", content = @Content(schema = @Schema(implementation = MovieDto.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta o datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado - se requiere autenticación"),
            @ApiResponse(responseCode = "415", description = "Tipo de contenido no soportado (verificar multipart/form-data)"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
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

    @SecurityRequirement(name = "auth-openapi")
    @Operation(
            summary = "Update an existing movie",
            description = "This endpoint allows updating a movie with an image, description, and general data. Requires authentication.",
            parameters = {
                    @Parameter(
                            name = "movieId",
                            description = "Identification movie in the system (default: 0)",
                            required = true,
                            example = "1"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(implementation = MovieFormRequestSchema.class, contentMediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
                    )
            ),
            method = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movie updated successfully", content = @Content(schema = @Schema(implementation = MovieDto.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta o datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado - se requiere autenticación"),
            @ApiResponse(responseCode = "415", description = "Tipo de contenido no soportado (verificar multipart/form-data)"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping(value="/updated/{movieId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
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
