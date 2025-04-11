package com.tokioschool.redis.services;

/**
 * Interfaz para el servicio de lista negra de JWT.
 *
 * Proporciona métodos para agregar tokens JWT a una lista negra y verificar si un token
 * está incluido en dicha lista.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public interface JwtBlacklistService {

    /**
     * Agrega un token JWT a la lista negra.
     *
     * @param token El token JWT que se agregará a la lista negra.
     * @param timeExpired El tiempo en milisegundos hasta que el token expire en la lista negra.
     */
    void addToBlacklist(String token, long timeExpired);

    /**
     * Verifica si un token JWT está en la lista negra.
     *
     * @param token El token JWT que se verificará.
     * @return {@code true} si el token está en la lista negra, de lo contrario {@code false}.
     */
    boolean isBlacklisted(String token);
}