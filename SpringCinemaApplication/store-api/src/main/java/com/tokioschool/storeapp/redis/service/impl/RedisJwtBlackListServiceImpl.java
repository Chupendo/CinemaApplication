package com.tokioschool.storeapp.redis.service.impl;

import com.tokioschool.storeapp.redis.service.RedisJwtBlackListService;
import com.tokioschool.storeapp.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Implementación del servicio para gestionar una lista negra de JWT en Redis.
 *
 * Esta clase proporciona métodos para agregar tokens a la lista negra y verificar
 * si un token está en la lista negra utilizando Redis como sistema de almacenamiento.
 *
 * Notas:
 * - Utiliza RedisTemplate para interactuar con Redis.
 * - La lista negra se gestiona como pares clave-valor en Redis, donde la clave es el token
 *   y el valor es una constante que indica que está en la lista negra.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class RedisJwtBlackListServiceImpl implements RedisJwtBlackListService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_KEY = "blacklisted";

    /**
     * Agrega un token a la lista negra en Redis.
     *
     * Este metodo crea un registro en Redis como un par clave-valor, donde la clave
     * es el token del usuario y el valor es la constante {@link #BLACKLIST_KEY}.
     * El registro tiene un tiempo de expiración definido.
     *
     * @param token Token que se agregará a la lista negra.
     * @param timeExpired Tiempo en milisegundos hasta que el registro expire en Redis.
     */
    public void addToBlacklist(String token, long timeExpired) {
        redisTemplate.opsForValue().set(token, BLACKLIST_KEY, timeExpired, TimeUnit.MILLISECONDS);
    }

    /**
     * Verifica si un token está en la lista negra.
     *
     * Este metodo consulta Redis para determinar si el token proporcionado
     * está presente en la lista negra.
     *
     * @param token Token que se verificará en la lista negra.
     * @return `true` si el token está en la lista negra, de lo contrario `false`.
     */
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }
}