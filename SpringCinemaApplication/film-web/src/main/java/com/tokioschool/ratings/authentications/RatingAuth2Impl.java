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

/**
 * Implementación del servicio de autenticación para obtener tokens de acceso OAuth2.
 *
 * Esta clase implementa la interfaz `RatingAuth2` y proporciona la lógica para
 * autenticar solicitudes y gestionar tokens de acceso en el sistema de calificaciones.
 *
 * Anotaciones utilizadas:
 * - `@Service`: Marca esta clase como un componente de servicio de Spring.
 * - `@Slf4j`: Habilita el registro de logs utilizando Lombok.
 * - `@RequiredArgsConstructor`: Genera un constructor con los argumentos requeridos.
 * - `@DependsOn`: Especifica que esta clase depende de la configuración `ratingPropertyConfig`.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
@DependsOn("ratingPropertyConfig")
public class RatingAuth2Impl implements RatingAuth2 {

    /** Propiedades de configuración para el sistema de calificaciones. */
    private final RatingProperty ratingProperty;

    /**
     * Cliente REST para realizar solicitudes HTTP.
     * Se inicializa en el metodo `init`.
     */
    private RestClient restClient;

    /**
     * Token de acceso actual.
     * Se actualiza al realizar una nueva autenticación.
     */
    private String accessToken = null;

    /**
     * Tiempo de expiración del token de acceso en milisegundos.
     * Se utiliza para determinar si el token actual sigue siendo válido.
     */
    private long expiresIn = 0;

    /**
     * Ruta del recurso para el endpoint de autenticación.
     */
    private static final String RESOURCE_PATH = "/oauth2/authenticate";

    /**
     * Inicializa el cliente REST con la URL base configurada.
     *
     * Este metodo se ejecuta después de la construcción de la clase.
     */
    @PostConstruct
    public void init() {
        restClient = RestClient.builder().baseUrl(ratingProperty.baseUrl()).build();
    }

    /**
     * Obtiene el token de acceso OAuth2.
     *
     * Si el token actual aún es válido, se devuelve directamente. De lo contrario,
     * se realiza una nueva solicitud al endpoint de autenticación para obtener un nuevo token.
     *
     * @return El token de acceso como una cadena de texto.
     * @throws RestClientResponseException Si ocurre un error en la solicitud al endpoint de autenticación.
     */
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
            // Realiza la solicitud al endpoint de autenticación
            OAuth2TokenResponse oAuth2TokenResponse = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(RESOURCE_PATH)
                            .queryParam("grant_type", "client_credentials")
                            .build())
                    .headers(h -> h.setBasicAuth(ratingProperty.client().user(), new String(decodedBytes)))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(OAuth2TokenResponse.class);

            // Verifica que la respuesta no sea nula
            if (Objects.isNull(oAuth2TokenResponse)) {
                throw new NullPointerException("Response is null");
            }

            // Actualiza el token de acceso y su tiempo de expiración
            accessToken = oAuth2TokenResponse.getAccessToken();
            expiresIn = System.currentTimeMillis() + oAuth2TokenResponse.getExpiresIn();

        } catch (RestClientResponseException e) {
            // Manejo de errores específicos del cliente REST
            HttpStatusCode status = e.getStatusCode();
            int statusValue = status.value();
            log.error("Auth endpoint returned {}: {}", statusValue, e.getResponseBodyAsString());
            throw e;

        } catch (Exception e) {
            // Manejo de excepciones inesperadas
            log.error("Unexpected exception in auth endpoint", e);
            return null;
        }

        return accessToken;
    }
}