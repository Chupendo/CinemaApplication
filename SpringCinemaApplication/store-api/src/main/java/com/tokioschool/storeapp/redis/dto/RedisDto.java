package com.tokioschool.storeapp.redis.dto;

import lombok.Builder;

/**
 * DTO que representa un par clave-valor para Redis.
 *
 * Esta clase utiliza un record para encapsular una clave y un valor,
 * proporcionando una estructura inmutable y sencilla.
 *
 * Notas:
 * - Es inmutable gracias al uso de `record`.
 * - Se construye utilizando el patrón Builder proporcionado por Lombok.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Builder
public record RedisDto(
        /**
         * Clave asociada al valor en Redis.
         */
        String key,

        /**
         * Valor asociado a la clave en Redis.
         */
        Object value) {
}