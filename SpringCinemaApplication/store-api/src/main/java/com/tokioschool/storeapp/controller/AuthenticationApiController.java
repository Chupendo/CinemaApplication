package com.tokioschool.storeapp.controller;

import com.tokioschool.storeapp.dto.authentication.AuthenticatedMeResponseDTO;
import com.tokioschool.storeapp.dto.authentication.AuthenticationRequestDTO;
import com.tokioschool.storeapp.dto.authentication.AuthenticationResponseDTO;
import com.tokioschool.storeapp.redis.service.RedisJwtBlackListService;
import com.tokioschool.storeapp.service.AuthenticationService;
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
import java.util.Map;

/**
 * Controlador para operaciones de autenticación en la aplicación de la tienda.
 *
 * Este controlador proporciona endpoints para manejar el inicio de sesión, obtener
 * información del usuario autenticado y cerrar sesión.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/store/api/auth")
@Tag(name = "authentication", description = "Operaciones relacionadas con la autenticación")
public class AuthenticationApiController {

    private final AuthenticationService authenticationService;
    private final RedisJwtBlackListService jwtBlacklistService;

    /**
     * Endpoint para autenticar un usuario.
     *
     * Este metodo procesa una solicitud de autenticación y devuelve un token JWT si las credenciales son válidas.
     *
     * @param authenticationRequestDTO Objeto {@link AuthenticationRequestDTO} con las credenciales del usuario.
     * @return Una respuesta HTTP con el token JWT y un código de estado 200 (OK).
     */
    @Operation(
            summary = "Autenticar un usuario",
            description = "Este endpoint permite autenticar un usuario y obtener un token JWT.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Autenticación exitosa",
                            content = @Content(schema = @Schema(implementation = AuthenticationResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Fallo en la autenticación",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            }
    )
    @PostMapping(value = {"", "/", "/login"}, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthenticationResponseDTO> postAuthenticated(@RequestBody AuthenticationRequestDTO authenticationRequestDTO) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequestDTO));
    }

    /**
     * Endpoint para obtener información del usuario autenticado.
     *
     * Este método devuelve los datos del usuario actualmente autenticado.
     *
     * @return Una respuesta HTTP con los datos del usuario autenticado y un código de estado 200 (OK).
     * @throws LoginException Si no se puede obtener la información del usuario autenticado.
     */
    @Operation(
            summary = "Obtener datos del usuario autenticado",
            description = "Este endpoint devuelve los datos del usuario actualmente autenticado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Datos del usuario autenticado obtenidos exitosamente",
                            content = @Content(schema = @Schema(implementation = AuthenticatedMeResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Acceso denegado, no autorizado",
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
    @GetMapping("/me")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthenticatedMeResponseDTO> getAuthenticatedMe() throws LoginException {
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