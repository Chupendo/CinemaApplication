package com.tokioschool.storeapp.dto.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * DTO que representa la respuesta de autenticación de un usuario.
 *
 * Esta clase contiene el token de acceso generado tras la autenticación
 * y el tiempo de expiración del mismo.
 *
 * Notas:
 * - Es inmutable gracias a las anotaciones de Lombok (@Value).
 * - Se utiliza la anotación @Jacksonized para facilitar la serialización/deserialización con Jackson.
 * - Se construye utilizando el patrón Builder.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Value
@Jacksonized
@Builder
public class AuthenticationResponseDTO {

    /**
     * Token de acceso generado tras la autenticación.
     *
     * Este campo se serializa/deserializa con la clave JSON "access_token".
     */
    @JsonProperty("access_token")
    String accessToken;

    /**
     * Tiempo en segundos hasta que el token de acceso expire.
     *
     * Este campo se serializa/deserializa con la clave JSON "expires_in".
     */
    @JsonProperty("expires_in")
    long expiresIn;
}