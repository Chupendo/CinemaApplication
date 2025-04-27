package com.tokioschool.store.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * DTO para la respuesta del endpoint de autenticación.
 *
 * @param accessToken El token de acceso obtenido.
 * @param expiresIn El tiempo de expiración del token en segundos.
 */
@Builder
public record AuthResponseDto(@JsonProperty("access_token") String accessToken,@JsonProperty("expires_in") long expiresIn) {
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