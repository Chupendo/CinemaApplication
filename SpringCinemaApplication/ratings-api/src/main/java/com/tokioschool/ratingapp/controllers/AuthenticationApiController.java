package com.tokioschool.ratingapp.controllers;

import com.tokioschool.filmapp.dto.auth.AuthenticatedMeResponseDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationRequestDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationResponseDTO;
import com.tokioschool.ratingapp.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import java.nio.file.AccessDeniedException;
import java.util.Map;

@RestController
@RequestMapping("/ratings/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication API", description = "API for user authentication")
public class AuthenticationApiController {

    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Post authenticated by authentication request dto",
            parameters = {
                    @Parameter(
                            name = "authenticationRequestDTO",
                            description = "User name or Email and Passwrod",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationRequestDTO.class))
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "authentication response dto",
                            content = @Content(schema = @Schema(implementation = AuthenticationResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "access denied, Unauthorized",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "don't allow, Forbidden ",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "error internal",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            }
    )
    @PostMapping(value = {"","/","/login"},consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<AuthenticationResponseDTO> loginHandler(@RequestBody AuthenticationRequestDTO authenticationRequestDTO) throws UsernameNotFoundException, BadCredentialsException, AccessDeniedException {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequestDTO) );
    }

    @Operation(
            summary = "Get data of authenticated user with role ADMIN",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "authentication me response dto",
                            content = @Content(schema = @Schema(implementation = AuthenticatedMeResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "access denied, Unauthorized",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "don't allow, Forbidden ",
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
    @GetMapping(value={"/me"},produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthenticatedMeResponseDTO> getAuthenticatedMe() throws LoginException {
        return ResponseEntity.ok(authenticationService.getAuthenticated());
    }

}
