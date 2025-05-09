package com.tokioschool.store.dto;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.Arrays;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) para representar el contenido de un recurso.
 *
 * Esta clase utiliza un registro para almacenar información sobre un recurso, incluyendo
 * su identificador, contenido, nombre, tipo de contenido, descripción y tamaño.
 *
 * Anotaciones:
 * - {@link Builder}: Proporciona un patrón de construcción para instanciar objetos de esta clase.
 * - {@link Jacksonized}: Permite la deserialización de objetos utilizando Jackson.
 *
 * Campos:
 * - {@code resourceId}: Identificador único del recurso.
 * - {@code content}: Contenido del recurso en formato de bytes.
 * - {@code resourceName}: Nombre del recurso.
 * - {@code contentType}: Tipo de contenido del recurso (por ejemplo, "image/png").
 * - {@code description}: Descripción del recurso.
 * - {@code size}: Tamaño del recurso en bytes.
 *
 * Métodos sobrescritos:
 * - {@link #equals(Object)}: Compara este objeto con otro para determinar si son iguales.
 * - {@link #hashCode()}: Calcula el código hash para este objeto.
 * - {@link #toString()}: Devuelve una representación en forma de cadena de este objeto.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Builder
@Jacksonized
public record ResourceContentDto(UUID resourceId, byte[] content, String resourceName, String contentType, String description, int size) {

    /**
     * Compara este objeto con otro para determinar si son iguales.
     *
     * @param o El objeto a comparar.
     * @return {@code true} si los objetos son iguales, de lo contrario {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceContentDto that = (ResourceContentDto) o;

        if (size != that.size) return false;
        if (!resourceId.equals(that.resourceId)) return false;
        if (!resourceName.equals(that.resourceName)) return false;
        return contentType.equals(that.contentType);
    }

    /**
     * Calcula el código hash para este objeto.
     *
     * @return El código hash calculado.
     */
    @Override
    public int hashCode() {
        int result = resourceId.hashCode();
        result = 31 * result + resourceName.hashCode();
        result = 31 * result + contentType.hashCode();
        result = 31 * result + size;
        return result;
    }

    /**
     * Devuelve una representación en forma de cadena de este objeto.
     *
     * @return Una cadena que representa este objeto.
     */
    @Override
    public String toString() {
        return "ResourceContentDto{" +
                "resourceId=" + resourceId +
                ", content=" + Arrays.toString(content) +
                ", resourceName='" + resourceName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", description='" + description + '\'' +
                ", size=" + size +
                '}';
    }
}