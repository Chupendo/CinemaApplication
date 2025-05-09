package com.tokioschool.store.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * DTO (Data Transfer Object) para describir un recurso.
 *
 * Esta clase modela un archivo en formato JSON que se guarda en cada carga al sistema de un recurso.
 *
 * Notas:
 * - Esta información no se guarda en la base de datos.
 * - El nombre de este archivo es el ID del mismo y es una referencia de UUID.
 * - El nombre del recurso es la combinación de su nombre y extensión.
 * - Los recursos se alojan en un directorio definido dentro de las propiedades de la aplicación.
 *
 * Anotaciones:
 * - {@link Value}: Marca la clase como inmutable y genera métodos como equals, hashCode y toString.
 * - {@link Builder}: Proporciona un patrón de construcción para instanciar objetos de esta clase.
 * - {@link Jacksonized}: Permite la deserialización de objetos utilizando Jackson.
 *
 * Campos:
 * - {@code description}: Descripción del recurso.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Value
@Builder
@Jacksonized
public class ResourceDescriptionDto {

    /**
     * Descripción del recurso.
     *
     * Este campo contiene información textual que describe el recurso.
     */
    private String description;
}