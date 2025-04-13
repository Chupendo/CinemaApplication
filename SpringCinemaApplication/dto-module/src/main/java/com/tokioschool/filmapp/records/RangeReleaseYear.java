package com.tokioschool.filmapp.records;

/**
 * Registro que representa un rango de años de lanzamiento de películas.
 *
 * Este registro encapsula la información relacionada con el rango de años
 * de lanzamiento, incluyendo el año mínimo y el año máximo.
 *
 * Es inmutable y se utiliza para transferir datos de manera eficiente.
 *
 * @param yearMin Año mínimo del rango de lanzamiento.
 * @param yearMax Año máximo del rango de lanzamiento.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public record RangeReleaseYear(Integer yearMin, Integer yearMax) {
}