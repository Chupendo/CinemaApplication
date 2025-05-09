package com.tokioschool.storeapp.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

/**
 * Representa la descripción de un recurso almacenado en el sistema.
 *
 * Esta clase modela un archivo en formato JSON que se guarda cada vez que se carga un recurso en el sistema.
 *
 * Notas:
 * - Esta información no se almacena en la base de datos.
 * - El nombre de este archivo es el ID del mismo y se utiliza como referencia de un UUID.
 * - El nombre del recurso es la combinación de su nombre y extensión.
 * - Los recursos se alojan en un directorio definido en las propiedades de la aplicación.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Value
@Builder
@Jacksonized
public class ResourceDescription {

    /**
     * Identificador único del recurso.
     */
    UUID id;

    /**
     * Nombre del recurso, que incluye su nombre y extensión.
     */
    String resourceName;

    /**
     * Tipo de contenido del recurso (por ejemplo, "image/png").
     */
    String contentType;

    /**
     * Descripción adicional del recurso.
     */
    String description;

    /**
     * Tamaño del recurso en bytes.
     */
    int size;
}