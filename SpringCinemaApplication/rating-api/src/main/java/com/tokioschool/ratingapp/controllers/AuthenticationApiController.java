package com.tokioschool.ratingapp.controllers;

import com.tokioschool.filmapp.dto.auth.AuthenticatedMeResponseDto;
import com.tokioschool.filmapp.dto.auth.AuthenticationRequestDto;
import com.tokioschool.filmapp.dto.auth.AuthenticationResponseDto;

import com.tokioschool.ratingapp.redis.services.JwtBlacklistService;
import com.tokioschool.ratingapp.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import java.util.Map;

/**
 * Controlador para la autenticación de usuarios en la API de Ratings.
 *
 * Proporciona endpoints para iniciar sesión, obtener información del usuario autenticado
 * y cerrar sesión invalidando el token JWT.
 *
 * Anotaciones:
 * - {@link RestController}: Marca esta clase como un controlador REST de Spring.
 * - {@link RequestMapping}: Define la ruta base para los endpoints de este controlador.
 * - {@link RequiredArgsConstructor}: Genera un constructor con los argumentos requeridos para los campos finales.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class AuthenticationApiController {

    private final AuthenticationService authenticationService;
    private final JwtBlacklistService jwtBlacklistService;

    /**
     * Endpoint para iniciar sesión.
     *
     * Este metodo permite a los usuarios autenticarse proporcionando sus credenciales.
     *
     * @param authenticationRequestDTO Objeto con las credenciales del usuario.
     * @return Una respuesta HTTP con el token JWT generado.
     * @throws Exception Si ocurre un error durante la autenticación.
     */
    @Operation(
            summary = "Iniciar sesión",
            description = "Este endpoint permite a los usuarios autenticarse proporcionando sus credenciales.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Autenticación exitosa",
                            content = @Content(schema = @Schema(implementation = AuthenticationResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Credenciales inválidas",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            }
    )
    @PostMapping(value = {"/login"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<AuthenticationResponseDto> loginApiHandler(@RequestBody AuthenticationRequestDto authenticationRequestDTO) throws Exception {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequestDTO));
    }

    /**
     * Endpoint para obtener información del usuario autenticado.
     *
     * Este metodo devuelve los detalles del usuario actualmente autenticado.
     *
     * @return Una respuesta HTTP con los detalles del usuario autenticado.
     * @throws LoginException Si ocurre un error al obtener la información del usuario.
     */
    @Operation(
            summary = "Obtener información del usuario autenticado",
            description = "Este endpoint devuelve los detalles del usuario actualmente autenticado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Información del usuario autenticado",
                            content = @Content(schema = @Schema(implementation = AuthenticatedMeResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuario no autenticado",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            },
            security = @SecurityRequirement(name = "auth-openapi")
    )
    @GetMapping(value = {"/me"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthenticatedMeResponseDto> getAuthenticatedMeHandler() throws LoginException {
        return ResponseEntity.ok(authenticationService.getAuthenticated());
    }

    /**
     * Endpoint para cerrar sesión.
     *
     * Este metodo permite al usuario cerrar sesión invalidando su token JWT.
     *
     * @param request Objeto {@link HttpServletRequest} que contiene el token JWT.
     * @return Una respuesta HTTP con un código de estado 200 (OK) si el cierre de sesión es exitoso.
     * @throws BadCredentialsException Si el token es inválido o ya está en la lista negra.
     */
    @Operation(
            summary = "Cerrar sesión",
            description = "Este endpoint permite al usuario cerrar sesión invalidando su token JWT.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cierre de sesión exitoso",
                            content = @Content(schema = @Schema(implementation = Void.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Acceso denegado o token inválido",
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
    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logoutHandler(HttpServletRequest request) throws BadCredentialsException {
        Pair<String, Long> tokenAndExpiredAt = authenticationService.getTokenAndExpiredAt(request);
        if (tokenAndExpiredAt != null && tokenAndExpiredAt.getLeft() != null && tokenAndExpiredAt.getRight() != null) {
            jwtBlacklistService.addToBlacklist(tokenAndExpiredAt.getLeft(), tokenAndExpiredAt.getRight());
            return ResponseEntity.ok().build();
        }
        throw new BadCredentialsException("Token inválido o en lista negra");
    }
}