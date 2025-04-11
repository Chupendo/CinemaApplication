package com.tokioschool.store.dto;

import lombok.Builder;

import java.util.UUID;

/**
 * DTO (Data Transfer Object) que representa el identificador único de un recurso.
 *
 * Esta clase encapsula un identificador de recurso en formato UUID.
 *
 * Anotaciones:
 * - {@link Builder}: Proporciona un patrón de construcción para instanciar objetos de esta clase.
 *
 * Campos:
 * - {@code resourceId}: Identificador único del recurso.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Builder
public record ResourceIdDto(UUID resourceId) {
}