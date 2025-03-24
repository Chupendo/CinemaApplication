package com.tokioschool.ratingapp.auth.resources;

import com.tokioschool.ratingapp.core.responses.OAuth2TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2TokenService {

    private final WebClient.Builder webClientBuilder;

    public OAuth2TokenResponse exchangeAuthorizationCodeForAccessToken(String basicAuth) {

        // URL del servidor de autorización
        String tokenUri = "http://127.0.0.1:9095/oauth2/token";

        // Crear las credenciales base64 para Basic Authentication (client_id:client_secret)
        String clientId = "oauth-client";
        String clientSecret = "secret3";
        String credentials = clientId + ":" + clientSecret;

        String encodedCredentials = Optional.ofNullable(basicAuth)
                .orElseGet(()->Base64.getEncoder().encodeToString(credentials.getBytes()));


        // Construir la solicitud POST con WebClient
        return webClientBuilder.build()
                .post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)  // Tipo de contenido para el formulario
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials)  // Autenticación básica
                .bodyValue("grant_type=client_credentials")  // Parámetros en el cuerpo
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    // Manejo de errores en caso de fallo de autorización (401, 403, etc.)
                    return response.bodyToMono(String.class)
                            .flatMap(errorBody -> {
                                throw new AccessDeniedException("Error en la autorización: " + errorBody);
                            });
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    // Manejo de errores del servidor (500, etc.)
                    return response.bodyToMono(String.class)
                            .flatMap(errorBody -> {
                                throw new RuntimeException("Error del servidor: " + errorBody);
                            });
                })
                .bodyToMono(OAuth2TokenResponse.class)  // Recibir el cuerpo de la respuesta en formato String (JSON)
                .block();  // Bloquear hasta obtener la respuesta
    }
}
