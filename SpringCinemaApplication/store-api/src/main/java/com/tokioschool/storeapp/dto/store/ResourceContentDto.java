package com.tokioschool.storeapp.dto.store;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.Arrays;
import java.util.UUID;

/**
 * DTO que modela los datos mínimos de un recurso junto con su contenido.
 *
 * Esta clase utiliza un record para representar un recurso con su contenido,
 * incluyendo información como el ID del recurso, el contenido en bytes, el nombre,
 * el tipo de contenido, una descripción y su tamaño.
 *
 * Notas:
 * - Es inmutable gracias al uso de `record`.
 * - Se utiliza la anotación @Jacksonized para facilitar la serialización/deserialización con Jackson.
 * - Se construye utilizando el patrón Builder.
 *
 * @version 1.0
 * @author
 */
@Builder
@Jacksonized
public record ResourceContentDto(
        /**
         * Identificador único del recurso.
         */
        UUID resourceId,

        /**
         * Contenido del recurso en formato de arreglo de bytes.
         */
        byte[] content,

        /**
         * Nombre del recurso.
         */
        String resourceName,

        /**
         * Tipo de contenido del recurso (por ejemplo, "image/png").
         */
        String contentType,

        /**
         * Descripción adicional del recurso.
         */
        String description,

        /**
         * Tamaño del recurso en bytes.
         */
        int size) {

    /**
     * Compara este objeto con otro para determinar si son iguales.
     *
     * @param o Objeto a comparar.
     * @return `true` si los objetos son iguales, de lo contrario `false`.
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
     * @return Código hash del objeto.
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
     * Devuelve una representación en forma de cadena del objeto.
     *
     * @return Representación en forma de cadena del objeto.
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