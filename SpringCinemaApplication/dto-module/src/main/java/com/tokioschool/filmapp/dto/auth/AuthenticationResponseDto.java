package com.tokioschool.filmapp.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Clase DTO (Data Transfer Object) para representar la respuesta de autenticación.
 *
 * Esta clase se utiliza para transferir los datos relacionados con la respuesta
 * de autenticación de un usuario, incluyendo el token de acceso y el tiempo de expiración.
 * Es inmutable y utiliza la anotación `@Value` de Lombok.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Value
@Jacksonized
@Builder
public class AuthenticationResponseDto {

    /**
     * Token de acceso proporcionado tras la autenticación.
     */
    @JsonProperty("access_token")
    String accessToken;

    /**
     * Tiempo en segundos hasta que el token de acceso expire.
     */
    @JsonProperty("expires_in")
    long expiresIn;
}