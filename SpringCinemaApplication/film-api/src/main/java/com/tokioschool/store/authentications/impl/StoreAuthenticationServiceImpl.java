package com.tokioschool.store.authentications.impl;

import com.tokioschool.store.authentications.StoreAuthenticationService;
import com.tokioschool.store.properties.StorePropertiesFilm;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementación del servicio de autenticación para la tienda.
 *
 * Esta clase proporciona métodos para obtener un token de acceso utilizando credenciales
 * almacenadas en las propiedades de la aplicación y realiza solicitudes a un endpoint de autenticación.
 *
 * Anotaciones:
 * - {@link Service}: Marca esta clase como un componente de servicio de Spring.
 * - {@link RequiredArgsConstructor}: Genera un constructor con los argumentos requeridos para los campos finales.
 * - {@link Slf4j}: Proporciona un logger para la clase.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StoreAuthenticationServiceImpl implements StoreAuthenticationService {

    /**
     * Propiedades de configuración relacionadas con la tienda.
     */
    private final StorePropertiesFilm storePropertiesFilm;

    /**
     * Codificador de contraseñas para manejar la codificación de las mismas.
     */
    private final PasswordEncoder passwordEncoder;

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
    //private static final String USERNAME_LOGIN_DEFAULT = "consumer";
    @Value("${application.store.login.users[0].username:consumer}")
    private String USERNAME_LOGIN_DEFAULT;

    /**
     * Obtiene el token de acceso utilizando el nombre de usuario predeterminado.
     *
     * @return El token de acceso.
     */
    @Override
    public synchronized String getAccessToken() {
        return getAccessToken(USERNAME_LOGIN_DEFAULT);
    }

    /**
     * Obtiene el token de acceso para un nombre de usuario específico.
     *
     * @param userName El nombre de usuario para el cual se obtendrá el token de acceso.
     * @return El token de acceso.
     */
    @Override
    public synchronized String getAccessToken(String userName) {
        log.debug("getAccessToken: userName={}", userName);
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
            AuthResponseDTO authResponseDTO = restClient.post()
                    .uri(("%s%s").formatted(storePropertiesFilm.baseUrl(), RESOURCE_PATH))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(authRequest)
                    .retrieve()
                    .body(AuthResponseDTO.class);

            accessToken = authResponseDTO.accessToken();
            expiresIn = authResponseDTO.expiresIn();

        } catch (Exception e) {
            log.error("Exception in file-store auth endpoint", e);
        }

        // Devuelve el token de acceso actual o nulo si no se pudo obtener uno nuevo
        return accessToken;
    }

    /**
     * DTO para la respuesta del endpoint de autenticación.
     *
     * @param accessToken El token de acceso obtenido.
     * @param expiresIn El tiempo de expiración del token en segundos.
     */
    @Builder
    public record AuthResponseDTO(String accessToken, long expiresIn) {
        /**
         * Calcula el tiempo de expiración del token en milisegundos.
         *
         * @return El tiempo de expiración ajustado en milisegundos.
         */
        public long getExpiresAt() {
            // Convierte expiresIn a milisegundos y resta 5 segundos para evitar problemas durante una solicitud
            return System.currentTimeMillis() + expiresIn * 1000L - 5 * 1000L;
        }
    }
}