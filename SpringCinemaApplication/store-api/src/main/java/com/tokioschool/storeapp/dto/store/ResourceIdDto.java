package com.tokioschool.storeapp.dto.store;

import lombok.Builder;

import java.util.UUID;

/**
 * DTO que representa el identificador único de un recurso.
 *
 * Esta clase utiliza un record para encapsular el ID de un recurso,
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
public record ResourceIdDto(
        /**
         * Identificador único del recurso.
         */
        UUID resourceId) {
}