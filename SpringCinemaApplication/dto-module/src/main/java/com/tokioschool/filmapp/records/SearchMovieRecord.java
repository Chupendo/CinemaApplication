package com.tokioschool.filmapp.records;

import lombok.Builder;
import org.springframework.lang.NonNull;

/**
 * Registro que representa los criterios de búsqueda de películas.
 *
 * Este registro encapsula la información necesaria para realizar una búsqueda
 * de películas, incluyendo el título, el rango de años de lanzamiento, la página
 * actual y el tamaño de página.
 *
 * Es inmutable y se utiliza para transferir datos de manera eficiente.
 *
 * @param title             Título de la película a buscar.
 * @param rangeReleaseYear  Rango de años de lanzamiento de las películas a buscar.
 * @param page              Número de la página actual en la búsqueda.
 * @param pageSize          Cantidad de resultados por página.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Builder
public record SearchMovieRecord(String title, @NonNull RangeReleaseYear rangeReleaseYear, Integer page, Integer pageSize) {
}