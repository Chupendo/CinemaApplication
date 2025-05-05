package com.tokioschool.ratings.authentications;

import com.tokioschool.ratings.configs.RatingProperty;
import com.tokioschool.ratings.core.OAuth2TokenResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Base64;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@DependsOn("ratingPropertyConfig")
public class RatingAuth2Impl implements RatingAuth2{

    private final RatingProperty ratingProperty;

    /**
     * Cliente REST para realizar solicitudes HTTP.
     */
    private RestClient restClient;

    @PostConstruct
    public void init(){
        restClient = RestClient.builder().baseUrl(ratingProperty.baseUrl()).build();
    }

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
    private static final String RESOURCE_PATH = "/oauth2/authenticate";
    @Override
    public String getTokenAccess() {

        // Comprueba si el token actual aún es válido
        if (System.currentTimeMillis() < expiresIn) {
            return accessToken;
        }

        // Decodifica la contraseña de Base64
        String pwdEncodeBase64 = ratingProperty.client().password();
        byte[] decodedBytes = Base64.getDecoder().decode(pwdEncodeBase64);

        try {
            OAuth2TokenResponse oAuth2TokenResponse = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(RESOURCE_PATH)
                            .queryParam("grant_type", "client_credentials")
                            .build())
                    .headers(h -> h.setBasicAuth( ratingProperty.client().user() , new String(decodedBytes) ))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(OAuth2TokenResponse.class);

            if( Objects.isNull( oAuth2TokenResponse )){
                throw new NullPointerException("Response is null");
            }
            accessToken = oAuth2TokenResponse.getAccessToken();
            expiresIn = System.currentTimeMillis() + oAuth2TokenResponse.getExpiresIn();

        } catch (RestClientResponseException e) {
            HttpStatusCode status = e.getStatusCode();           // obtiene HttpStatusCode
            int statusValue = status.value();                    // código numérico
            log.error("Auth endpoint returned {}: {}",
                    statusValue,
                    e.getResponseBodyAsString());
            throw e;

        } catch (Exception e) {
            log.error("Unexpected exception in auth endpoint", e);
            return null;
        }

        return accessToken;
    }


}
