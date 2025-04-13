package com.tokioschool.ratingapp.controllers;

import com.tokioschool.ratingapp.auth.resources.OAuth2TokenService;
import com.tokioschool.ratingapp.core.responses.OAuth2TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para gestionar el flujo de autenticación OAuth2.
 *
 * Este controlador proporciona endpoints para manejar la autorización y obtener tokens de acceso.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@RestController
@Slf4j
@Tag(name = "OAuth2 Login", description = "Operaciones relacionadas con el inicio de sesión OAuth2")
public class LoginOAuth2ApiController {

    private final OAuth2TokenService oAuth2TokenService;

    /**
     * Constructor del controlador.
     *
     * @param oAuth2TokenService Servicio para manejar los tokens OAuth2.
     */
    public LoginOAuth2ApiController(OAuth2TokenService oAuth2TokenService) {
        this.oAuth2TokenService = oAuth2TokenService;
    }

    /**
     * Endpoint para manejar la autorización desde un cliente web OIDC.
     *
     * @param code Código de autorización recibido del proveedor OAuth2.
     * @return Respuesta HTTP con el token de acceso.
     * @throws BadRequestException Si el código de autorización es inválido.
     */
    @Operation(
            summary = "Autorizar cliente web OIDC",
            description = "Obtiene un token de acceso utilizando el código de autorización proporcionado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Token de acceso obtenido exitosamente",
                            content = @Content(schema = @Schema(implementation = OAuth2TokenResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud inválida",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @GetMapping("/login/oauth2/code/oidc-client-web")
    public ResponseEntity<OAuth2TokenResponse> authorizationWeb(@RequestParam(value = "code") String code) throws BadRequestException {
        log.debug("authorizaction: " + code);
        return ResponseEntity.ok(oAuth2TokenService.getAccessToken(code));
    }

    /**
     * Endpoint para manejar la autorización desde un cliente OIDC.
     *
     * @param code Código de autorización recibido del proveedor OAuth2.
     * @return Respuesta HTTP con el código de autorización.
     * @throws BadRequestException Si el código de autorización es inválido.
     */
    @Operation(
            summary = "Autorizar cliente OIDC",
            description = "Devuelve el código de autorización recibido del cliente.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Código de autorización recibido exitosamente",
                            content = @Content(mediaType = "text/plain")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud inválida",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @GetMapping("/login/oauth2/code/oidc-client")
    public ResponseEntity<String> authorization(@RequestParam(value = "code") String code) throws BadRequestException {
        log.debug("authorizaction: " + code);
        return ResponseEntity.ok(code);
    }
}