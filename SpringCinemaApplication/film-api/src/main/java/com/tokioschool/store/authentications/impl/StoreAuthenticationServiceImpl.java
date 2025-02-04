package com.tokioschool.store.authentications.impl;

import com.tokioschool.store.authentications.StoreAuthenticationService;
import com.tokioschool.store.properties.StorePropertiesFilm;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreAuthenticationServiceImpl implements StoreAuthenticationService {

    private final StorePropertiesFilm storePropertiesFilm;
    private final PasswordEncoder passwordEncoder;

    @Qualifier("restClientEmpty")
    private final RestClient restClient;

    private String   accessToken   =   null;
    private long     expiresIn        =   0;

    private static final String RESOURCE_PATH   =   "/store/api/auth";
    private static final String USERNAME_LOGIN_DEFAULT  =   "consumer";

    @Override
    public synchronized String getAccessToken() {
        return getAccessToken(USERNAME_LOGIN_DEFAULT);
    }

    @Override
    public synchronized String getAccessToken(String userName) {
        final String filterUserName = Optional.ofNullable(userName)
                .map(StringUtils::trimToNull)
                .map(String::toLowerCase)
                .orElseGet(() -> USERNAME_LOGIN_DEFAULT);

        // comprueba si es valdio el token aun
        if( System.currentTimeMillis() < expiresIn ) {
            return accessToken;
        }

        StorePropertiesFilm.UserStore userStore = storePropertiesFilm.login().users()
                .stream()
                .filter(usr -> Objects.equals(usr.username().toLowerCase(),filterUserName))
                .findFirst().orElseThrow(() -> new RuntimeException("No user found"));

        // realiza la peticion
        // Decodificar de Base64
        byte[] decodedBytes = Base64.getDecoder().decode(userStore.password());

        Map<String,String> authRequest = Map.of(
                "username",userStore.username(),
                "password",new String(decodedBytes)
        );

        try{
            AuthResponseDTO authResponseDTO = restClient.post()
                    .uri(("%s%s").formatted(storePropertiesFilm.baseUrl(),RESOURCE_PATH))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(authRequest)
                    .retrieve()
                    .body(AuthResponseDTO.class);

            accessToken = authResponseDTO.accessToken();
            expiresIn = authResponseDTO.expiresIn();

        }catch (Exception e) {
            log.error("Exception in file-store auth endpoint", e);
        }

        // delvelra el token bueno, el viejo o nulo
        return accessToken;
    }

    @Builder
    protected record AuthResponseDTO(String accessToken, long expiresIn) {
        public long getExpiresAt() {
            // expiresIn se pasa a segundos y se resta 5 segudnos para que no pille en medio de una peticion
            return System.currentTimeMillis() + expiresIn * 1000L - 5*1000L;
        }
    }
}
