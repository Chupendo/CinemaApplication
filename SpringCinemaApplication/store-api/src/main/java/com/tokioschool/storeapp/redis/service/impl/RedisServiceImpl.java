package com.tokioschool.storeapp.redis.service.impl;

import com.tokioschool.storeapp.redis.dto.RedisDto;
import com.tokioschool.storeapp.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio para interactuar con Redis.
 *
 * Esta clase proporciona métodos para guardar y recuperar valores en Redis
 * utilizando pares clave-valor.
 *
 * Notas:
 * - Utiliza RedisTemplate para interactuar con Redis.
 * - Los valores se almacenan como objetos genéricos en Redis.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Guarda un registro en Redis como un par clave-valor.
     *
     * Este metodo utiliza RedisTemplate para almacenar un valor asociado a una clave
     * proporcionada en el objeto RedisDto.
     *
     * @param redisDto Par clave-valor que se guardará en Redis.
     */
    public void saveValue(RedisDto redisDto) {
        redisTemplate.opsForValue().set(redisDto.key(), redisDto.value());
    }

    /**
     * Recupera el valor asociado a una clave específica en Redis.
     *
     * Este metodo consulta Redis para obtener el valor asociado a la clave proporcionada.
     *
     * @param key Clave utilizada para buscar el registro en Redis.
     * @return El valor asociado a la clave como una cadena de texto, o `null` si no se encuentra.
     */
    public String getValue(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }
}