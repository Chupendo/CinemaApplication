package com.tokioschool.storeapp.redis.service;

import com.tokioschool.storeapp.redis.dto.RedisDto;

/**
 * Interfaz para interactuar con Redis.
 *
 * Esta interfaz define los métodos necesarios para guardar y recuperar valores
 * en Redis utilizando pares clave-valor.
 *
 * Notas:
 * - Los valores se almacenan como objetos genéricos en Redis.
 * - La implementación de esta interfaz debe manejar la lógica de interacción con Redis.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public interface RedisService {

    /**
     * Guarda un registro en Redis como un par clave-valor.
     *
     * Este metodo debe almacenar un valor asociado a una clave proporcionada
     * en el objeto RedisDto.
     *
     * @param redisDto Par clave-valor que se guardará en Redis.
     */
    void saveValue(RedisDto redisDto);

    /**
     * Recupera el valor asociado a una clave específica en Redis.
     *
     * Este metodo debe consultar Redis para obtener el valor asociado a la clave proporcionada.
     *
     * @param clave Clave utilizada para buscar el registro en Redis.
     * @return El valor asociado a la clave como una cadena de texto, o `null` si no se encuentra.
     */
    String getValue(String clave);
}