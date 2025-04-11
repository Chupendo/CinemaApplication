package com.tokioschool.ratingapp.controllers;

import com.tokioschool.ratingapp.auth.resources.OAuth2TokenService;
import com.tokioschool.ratingapp.core.responses.OAuth2TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para gestionar la autenticación OAuth2.
 *
 * Este controlador proporciona endpoints para autenticar clientes utilizando el flujo de credenciales de cliente.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@RestController
@Slf4j
@RequestMapping("/oauth2")
@RequiredArgsConstructor
@Tag(name = "OAuth2 Authorization", description = "Operaciones relacionadas con la autenticación OAuth2")
public class OAuth2AuthorizationController {

    private final OAuth2TokenService oAuth2TokenService;

    /**
     * Endpoint para autenticar un cliente utilizando el flujo de credenciales de cliente.
     *
     * @param authorization Cabecera de autorización en formato Basic.
     * @param grantType Tipo de concesión, debe ser "client_credentials".
     * @return Respuesta HTTP con el token de acceso.
     * @throws BadRequestException Si el tipo de concesión o la cabecera de autorización son inválidos.
     */
    @Operation(
            summary = "Autenticar cliente con client_credentials",
            description = "Obtiene un token de acceso utilizando el flujo de credenciales de cliente.",
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
    @GetMapping("/authenticate")
    public ResponseEntity<OAuth2TokenResponse> authenticate(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam("grant_type") String grantType) throws BadRequestException {
        log.debug("authorizaction: " + authorization + ", grant_type: " + grantType);
        if (!"client_credentials".equals(grantType) || !authorization.startsWith("Basic ")) {
            throw new BadRequestException("error in grant type or authorization");
        }

        return ResponseEntity.ok(oAuth2TokenService.exchangeAuthorizationCodeForAccessToken(authorization.replace("Basic ", "")));
    }
}