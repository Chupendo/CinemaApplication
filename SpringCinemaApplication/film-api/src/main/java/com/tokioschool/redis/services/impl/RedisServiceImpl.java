package com.tokioschool.redis.services.impl;

import com.tokioschool.redis.services.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio Redis.
 *
 * Proporciona métodos para interactuar con Redis, permitiendo guardar y recuperar valores
 * utilizando claves específicas.
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
public class RedisServiceImpl implements RedisService {

    /**
     * Plantilla de Redis para realizar operaciones en la base de datos Redis.
     */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Guarda un valor en Redis asociado a una clave específica.
     *
     * @param key La clave con la que se asociará el valor.
     * @param value El valor que se guardará en Redis.
     */
    public void saveValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * Recupera un valor de Redis utilizando una clave específica.
     *
     * @param key La clave asociada al valor que se desea recuperar.
     * @return El valor asociado a la clave, o {@code null} si no existe.
     */
    public String getValue(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }
}