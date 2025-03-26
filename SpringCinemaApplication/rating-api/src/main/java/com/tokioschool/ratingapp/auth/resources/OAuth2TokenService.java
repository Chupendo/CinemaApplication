package com.tokioschool.ratingapp.auth.resources;

import com.tokioschool.ratingapp.auth.configs.OauthClientProperty;
import com.tokioschool.ratingapp.core.responses.OAuth2TokenResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2TokenService {

    private final WebClient.Builder webClientBuilder;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final PasswordEncoder passwordEncoder;

    private final OauthClientProperty oauthClientProperty;

    /**
     *
     * @param basicAuthCredentials param with "clientId:clientSecret", encoded in Base 64
     * @return
     */
    public OAuth2TokenResponse exchangeAuthorizationCodeForAccessToken(String basicAuthCredentials) {

        // URL del servidor de autorización
        String tokenUri = oauthClientProperty.tokenUri();

        String credentials = Optional.ofNullable(basicAuthCredentials)
                .map(Base64.getDecoder()::decode)
                .map(bytes -> StringUtils.toEncodedString(bytes, StandardCharsets.UTF_8))
                .orElseGet(()->null);

        if(credentials == null){
            throw new AccessDeniedException("credentials not allowed.");
        }

        final String[] credentialsArray = credentials.split(":");
        final String clientId = credentialsArray[0];
        final String clientSecret = credentialsArray[1];


        // Crear las credenciales base64 para Basic Authentication (client_id:client_secret)
        final ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId( clientId );


        if( clientRegistration == null || !clientRegistration.getClientId().equals(clientId)
        || !passwordEncoder.matches(clientSecret,clientRegistration.getClientSecret()) ){
            throw new BadCredentialsException("bad credentials in authentication.");
        }

        // Construir la solicitud POST con WebClient
        return webClientBuilder.build()
                .post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)  // Tipo de contenido para el formulario
                .header(HttpHeaders.AUTHORIZATION, "Basic %s".formatted( basicAuthCredentials) ) // Autenticación básica
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
