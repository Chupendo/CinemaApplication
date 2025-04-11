package com.tokioschool.filmapp.controller;

import com.tokioschool.filmapp.dto.auth.AuthenticatedMeResponseDto;
import com.tokioschool.filmapp.dto.auth.AuthenticationRequestDto;
import com.tokioschool.filmapp.dto.auth.AuthenticationResponseDto;
import com.tokioschool.filmapp.services.auth.AuthenticationService;
import com.tokioschool.redis.services.JwtBlacklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import java.text.ParseException;
import java.util.Map;

/**
 * Controlador REST para gestionar operaciones de autenticación.
 *
 * Este controlador proporciona endpoints para iniciar sesión, obtener información
 * del usuario autenticado y cerrar sesión.
 *
 * Anotaciones:
 * - {@link RestController}: Indica que esta clase es un controlador REST.
 * - {@link RequestMapping}: Define la ruta base para los endpoints de este controlador.
 * - {@link Tag}: Proporciona metadatos para la documentación de Swagger.
 *
 * Dependencias:
 * - {@link AuthenticationService}: Servicio para manejar la lógica de autenticación.
 * - {@link JwtBlacklistService}: Servicio para gestionar el listado negro de tokens JWT.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/film/api/auth")
@Tag(name = "authentication", description = "authentication operations")
public class AuthenticationApiController {

    private final AuthenticationService authenticationService;
    private final JwtBlacklistService jwtBlacklistService;

    /**
     * Endpoint para autenticar a un usuario.
     *
     * Este metodo recibe las credenciales del usuario, las valida y devuelve un token JWT.
     *
     * @param authenticationRequestDTO Objeto {@link AuthenticationRequestDto} con las credenciales del usuario.
     * @return Una respuesta HTTP con el token JWT y un código de estado 200 (OK).
     * @throws ParseException Si ocurre un error al procesar el token.
     */
    @Operation(
            summary = "Post authenticated by authentication request dto",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "authentication response dto",
                            content = @Content(schema = @Schema(implementation = AuthenticationResponseDto.class))
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
    @PostMapping(value = {"", "/", "/login"}, produces = "application/json", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AuthenticationResponseDto> postAuthenticated(@RequestBody AuthenticationRequestDto authenticationRequestDTO) throws ParseException {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequestDTO));
    }

    /**
     * Endpoint para obtener información del usuario autenticado.
     *
     * Este metodo devuelve los datos del usuario actualmente autenticado.
     *
     * @return Una respuesta HTTP con los datos del usuario autenticado y un código de estado 200 (OK).
     */
    @Operation(
            summary = "Get data of authenticated user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "authentication me response dto",
                            content = @Content(schema = @Schema(implementation = AuthenticatedMeResponseDto.class))
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
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "auth-openapi")
    public ResponseEntity<AuthenticatedMeResponseDto> getAuthenticatedMe() {
        return ResponseEntity.ok(authenticationService.getAuthenticated());
    }

    /**
     * Endpoint para cerrar sesión.
     *
     * Este metodo invalida el token JWT del usuario autenticado y lo agrega a la lista negra.
     *
     * @param request Objeto {@link HttpServletRequest} que contiene el token JWT.
     * @return Una respuesta HTTP con un código de estado 200 (OK) si el cierre de sesión es exitoso.
     * @throws LoginException Si el token es inválido o ya está en la lista negra.
     */
    @Operation(
            summary = "Get or Post Logout the user of system",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "authentication me response dto",
                            content = @Content(schema = @Schema(implementation = AuthenticatedMeResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "access denied or secret invalid",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "error internal",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            }
    )
    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "auth-openapi")
    public ResponseEntity<Void> logoutHandler(HttpServletRequest request) throws LoginException {
        Pair<String, Long> tokenAndExpiredAt = authenticationService.getTokenAndExpiredAt(request);
        if (tokenAndExpiredAt != null && tokenAndExpiredAt.getLeft() != null && tokenAndExpiredAt.getRight() != null) {
            jwtBlacklistService.addToBlacklist(tokenAndExpiredAt.getLeft(), tokenAndExpiredAt.getRight());
            return ResponseEntity.ok().build();
        }
        throw new BadCredentialsException("invalid secret is black listed");
    }
}