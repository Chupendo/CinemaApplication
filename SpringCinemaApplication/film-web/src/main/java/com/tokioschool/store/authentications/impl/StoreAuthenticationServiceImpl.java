package com.tokioschool.store.authentications.impl;

import com.tokioschool.store.authentications.StoreAuthenticationService;
import com.tokioschool.store.dto.AuthResponseDto;
import com.tokioschool.store.properties.StorePropertiesFilm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreAuthenticationServiceImpl implements StoreAuthenticationService {

    private final StorePropertiesFilm storePropertiesFilm;

    /**
     * Cliente REST para realizar solicitudes HTTP.
     */
    @Qualifier("restClientEmpty")
    private final RestClient restClient;

    /**
     * Token de acceso actual.
     */
    private String accessToken = null;

    /**
     * Tiempo de expiración del token de acceso en milisegundos.
     */
    private long expiresIn = 0;

    /**
     * Ruta del recurso para el endpoint de autenticación.
     */
    private static final String RESOURCE_PATH = "/store/api/auth";

    /**
     * Nombre de usuario predeterminado para el inicio de sesión.
     */
    private static final String USERNAME_LOGIN_DEFAULT = "consumer";

    @Override
    public String getAccessToken() {
        return getAccessToken(USERNAME_LOGIN_DEFAULT);

    }

    @Override
    public String getAccessToken(String userName) {
        final String filterUserName = Optional.ofNullable(userName)
                .map(StringUtils::trimToNull)
                .map(String::toLowerCase)
                .orElseGet(() -> USERNAME_LOGIN_DEFAULT);

        // Comprueba si el token actual aún es válido
        if (System.currentTimeMillis() < expiresIn) {
            return accessToken;
        }

        // Busca el usuario en las propiedades de configuración
        StorePropertiesFilm.UserStore userStore = storePropertiesFilm.login().users()
                .stream()
                .filter(usr -> Objects.equals(usr.username().toLowerCase(), filterUserName))
                .findFirst().orElseThrow(() -> new RuntimeException("No user found"));

        // Decodifica la contraseña de Base64
        byte[] decodedBytes = Base64.getDecoder().decode(userStore.password());

        Map<String, String> authRequest = Map.of(
                "username", userStore.username(),
                "password", new String(decodedBytes)
        );

        try {
            // Realiza la solicitud al endpoint de autenticación
            AuthResponseDto authResponseDto = restClient.post()
                    .uri(("%s%s").formatted(storePropertiesFilm.baseUrl(), RESOURCE_PATH))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(authRequest)
                    .retrieve()
                    .body(AuthResponseDto.class);

            if( Objects.isNull( authResponseDto )){
                throw new NullPointerException("Response is null");
            }
            accessToken = authResponseDto.accessToken();
            expiresIn = authResponseDto.expiresIn();

        } catch (Exception e) {
            log.error("Exception in file-store auth endpoint", e);
        }

        // Devuelve el token de acceso actual o nulo si no se pudo obtener uno nuevo
        return accessToken;
    }
}

