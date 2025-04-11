package com.tokioschool.storeapp.redis.service;

/**
 * Interfaz para gestionar una lista negra de JWT en Redis.
 *
 * Esta interfaz define los métodos necesarios para agregar tokens a la lista negra
 * y verificar si un token está en la lista negra.
 *
 * Notas:
 * - La implementación de esta interfaz debe interactuar con Redis para almacenar
 *   y consultar los tokens en la lista negra.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public interface RedisJwtBlackListService {

    /**
     * Agrega un token a la lista negra en Redis.
     *
     * Este metodo debe almacenar el token en Redis con un tiempo de expiración definido.
     *
     * @param token Token que se agregará a la lista negra.
     * @param timeExpired Tiempo en milisegundos hasta que el token expire en Redis.
     */
    void addToBlacklist(String token, long timeExpired);

    /**
     * Verifica si un token está en la lista negra.
     *
     * Este metodo debe consultar Redis para determinar si el token proporcionado
     * está presente en la lista negra.
     *
     * @param token Token que se verificará en la lista negra.
     * @return `true` si el token está en la lista negra, de lo contrario `false`.
     */
    boolean isBlacklisted(String token);
}