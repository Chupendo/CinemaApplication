package com.tokioschool.filmapp.controller;

import com.tokioschool.filmapp.dto.auth.AuthenticatedMeResponseDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationRequestDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationResponseDTO;
import com.tokioschool.filmapp.services.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/film/api/auth")
@Tag(name="authentication", description= "authentication operations")
public class AuthenticationApiController {

    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Post authenticated by authentication request dto",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "authentication response dto",
                            content = @Content(schema = @Schema(implementation = AuthenticationResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "authentication failed",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "error internal",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<AuthenticationResponseDTO> postAuthenticated(@RequestBody AuthenticationRequestDTO authenticationRequestDTO) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequestDTO));
    }

    @Operation(
            summary = "Get data of authenticated user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "authentication me response dto",
                            content = @Content(schema = @Schema(implementation = AuthenticatedMeResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "access denied",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "error internal",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            }
    )
    @GetMapping("/me")
    @SecurityRequirement(name = "auth-openapi")
    public ResponseEntity<AuthenticatedMeResponseDTO> getAuthenticatedMe() {
        return ResponseEntity.ok(authenticationService.getAuthenticated());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public Map<String, String> handleBadCredentialsExceptionError(BadCredentialsException ex, HttpServletRequest request) {
        return Map.of("message", ex.getMessage(),"request",request.getRequestURI());
    }
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public Map<String, String> handleInternalServerError(Exception ex, HttpServletRequest request) {
        return Map.of("message", ex.getMessage(),"request",request.getRequestURI());
    }
}
