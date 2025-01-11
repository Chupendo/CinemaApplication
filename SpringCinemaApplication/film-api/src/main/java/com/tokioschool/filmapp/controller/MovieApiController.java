package com.tokioschool.filmapp.controller;

import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.movie.FilterMovie;
import com.tokioschool.filmapp.dto.movie.MovieDto;
import com.tokioschool.filmapp.records.RangeReleaseYear;
import com.tokioschool.filmapp.records.SearchMovieRecord;
import com.tokioschool.filmapp.services.movie.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/film/api/movies")
@Tag(name="movies", description= "movies operations")
public class MovieApiController {

    private final MovieService movieService;

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
}
