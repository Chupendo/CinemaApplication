package com.tokioschool.filmapp.dto.common;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Clase DTO (Data Transfer Object) para representar una página de resultados.
 *
 * Esta clase se utiliza para transferir datos paginados, incluyendo los elementos
 * de la página, el tamaño de la página, el número de página actual y el total de páginas.
 * Es inmutable y utiliza la anotación `@Value` de Lombok.
 *
 * @param <T> el tipo de los elementos contenidos en la página.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Builder
@Value
@Jacksonized
public class PageDTO<T> {

    /**
     * Lista de elementos contenidos en la página.
     */
    List<T> items;

    /**
     * Tamaño de la página (número de elementos por página).
     */
    int pageSize;

    /**
     * Número de la página actual (comenzando desde 0).
     */
    int pageNumber;

    /**
     * Número total de páginas disponibles.
     */
    int totalPages;
}