package com.tokioschool.ratingapp.redis.services.impl;

import com.tokioschool.ratingapp.redis.services.JwtBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Implementación del servicio de lista negra de JWT.
 *
 * Esta clase utiliza Redis para gestionar una lista negra de tokens JWT, permitiendo
 * agregar tokens a la lista negra y verificar si un token está en ella.
 *
 * Anotaciones:
 * - {@link Service}: Marca esta clase como un componente de servicio de Spring.
 * - {@link RequiredArgsConstructor}: Genera un constructor con los argumentos requeridos para los campos finales.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class JwtBlacklistServiceImpl implements JwtBlacklistService {

    /**
     * Plantilla de Redis para realizar operaciones en la base de datos Redis.
     */
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Clave utilizada para identificar los tokens en la lista negra.
     */
    private static final String BLACKLIST_KEY = "blacklisted";

    /**
     * Agrega un token a la lista negra en Redis con un tiempo de expiración.
     *
     * @param token El token JWT que se agregará a la lista negra.
     * @param timeExpired El tiempo en milisegundos hasta que el token expire en la lista negra.
     */
    public void addToBlacklist(String token, long timeExpired) {
        redisTemplate.opsForValue().set(token, BLACKLIST_KEY, timeExpired, TimeUnit.MILLISECONDS);
    }

    /**
     * Verifica si un token está en la lista negra.
     *
     * @param token El token JWT que se verificará.
     * @return {@code true} si el token está en la lista negra, de lo contrario {@code false}.
     */
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }
}