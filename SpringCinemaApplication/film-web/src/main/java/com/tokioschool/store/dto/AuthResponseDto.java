package com.tokioschool.store.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * DTO para la respuesta del endpoint de autenticación.
 *
 * Esta clase representa la estructura de la respuesta obtenida al autenticar
 * un usuario en el sistema. Contiene el token de acceso y el tiempo de expiración.
 *
 * Anotaciones utilizadas:
 * - `@Builder`: Permite construir instancias de esta clase utilizando el patrón Builder.
 * - `@JsonProperty`: Mapea los nombres de las propiedades JSON a los campos de esta clase.
 *
 * Campos:
 * - `accessToken`: El token de acceso obtenido tras la autenticación.
 * - `expiresIn`: El tiempo de expiración del token en segundos.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Builder
public record AuthResponseDto(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") long expiresIn) {

    /**
     * Calcula el tiempo de expiración del token en milisegundos.
     *
     * Este metodo convierte el tiempo de expiración del token de segundos a milisegundos
     * y resta 5 segundos para evitar problemas durante una solicitud cercana al vencimiento.
     *
     * @return El tiempo de expiración ajustado en milisegundos.
     */
    public long getExpiresAt() {
        return System.currentTimeMillis() + expiresIn * 1000L - 5 * 1000L;
    }
}