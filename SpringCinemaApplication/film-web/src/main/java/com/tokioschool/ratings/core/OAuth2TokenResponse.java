package com.tokioschool.ratings.core;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Clase que representa la respuesta de un token OAuth2.
 *
 * Contiene información sobre el token de acceso, tipo de token, tiempo de expiración,
 * token de actualización y el alcance.
 *
 * Anotaciones utilizadas:
 * - `@JsonProperty`: Mapea los campos de la clase con las claves correspondientes
 *   en la respuesta JSON.
 *
 * Campos:
 * - `accessToken`: El token de acceso proporcionado por el servidor OAuth2.
 * - `tokenType`: El tipo de token (por ejemplo, "Bearer").
 * - `expiresIn`: El tiempo de expiración del token en segundos.
 * - `refreshToken`: El token de actualización para obtener un nuevo token de acceso.
 * - `scope`: El alcance asociado al token de acceso.
 *
 * Métodos:
 * - Métodos getter y setter para cada campo.
 * - `toString`: Representación en cadena de la clase.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class OAuth2TokenResponse {

    /** El token de acceso proporcionado por el servidor OAuth2. */
    @JsonProperty("access_token")
    private String accessToken;

    /** El tipo de token (por ejemplo, "Bearer"). */
    @JsonProperty("token_type")
    private String tokenType;

    /** El tiempo de expiración del token en segundos. */
    @JsonProperty("expires_in")
    private int expiresIn;

    /** El token de actualización para obtener un nuevo token de acceso. */
    @JsonProperty("refresh_token")
    private String refreshToken;

    /** El alcance asociado al token de acceso. */
    @JsonProperty("scope")
    private String scope;

    // Getters y Setters

    /**
     * Obtiene el token de acceso.
     *
     * @return El token de acceso.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Establece el token de acceso.
     *
     * @param accessToken El token de acceso.
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Obtiene el tipo de token.
     *
     * @return El tipo de token.
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     * Establece el tipo de token.
     *
     * @param tokenType El tipo de token.
     */
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    /**
     * Obtiene el tiempo de expiración del token.
     *
     * @return El tiempo de expiración en segundos.
     */
    public int getExpiresIn() {
        return expiresIn;
    }

    /**
     * Establece el tiempo de expiración del token.
     *
     * @param expiresIn El tiempo de expiración en segundos.
     */
    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    /**
     * Obtiene el token de actualización.
     *
     * @return El token de actualización.
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Establece el token de actualización.
     *
     * @param refreshToken El token de actualización.
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * Obtiene el alcance asociado al token.
     *
     * @return El alcance del token.
     */
    public String getScope() {
        return scope;
    }

    /**
     * Establece el alcance asociado al token.
     *
     * @param scope El alcance del token.
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Representación en cadena de la clase.
     *
     * @return Una cadena que representa los valores de los campos de la clase.
     */
    @Override
    public String toString() {
        return "OAuth2TokenResponse{" +
                "accessToken='" + accessToken + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", refreshToken='" + refreshToken + '\'' +
                ", scope=" + scope +
                '}';
    }
}