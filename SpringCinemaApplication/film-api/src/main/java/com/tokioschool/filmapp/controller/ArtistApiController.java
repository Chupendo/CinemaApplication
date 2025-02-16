package com.tokioschool.filmapp.controller;


import com.tokioschool.core.exception.ValidacionException;
import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.dto.auth.AuthenticationResponseDTO;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/film/api/artists")
@Tag(name="artist", description= "artist operations")
public class ArtistApiController {

    private final ArtistService artistService;

    @Operation(
            summary = "Post register artist in the system",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "register artist",
                            content = @Content(schema = @Schema(implementation = AuthenticationResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "register artist failed",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "request don't allow",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "406",
                            description = "the data of artist not allow",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "error internal",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            }
    )
    @SecurityRequirement(name = "auth-openapi")
    @PostMapping(value={"","/","/register"},consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ArtistDto> registerArtistDto(@Valid @RequestBody ArtistDto artistDto, BindingResult bindingResult){
        if( bindingResult.hasErrors() ){
            Map<String, String> errores = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage
                    ));
            throw new ValidacionException("Errores de validación", errores);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body( artistService.registerArtist(artistDto) );
    }

    @Operation(
            summary = "Post register artist in the system",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "register artist",
                            content = @Content(schema = @Schema(implementation = AuthenticationResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "request don't allow",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "error internal",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            }
    )
    @GetMapping(value={"/find-all"},produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ArtistDto>> findAllArtists(){

        return ResponseEntity.ok( artistService.findByAll() );
    }
}
