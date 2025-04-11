package com.tokioschool.filmapp.projections;

/**
 * Proyección que representa los datos básicos de una película.
 *
 * Esta interfaz define los métodos para obtener y establecer el título
 * y el año de lanzamiento de una película. Se utiliza para mapear
 * consultas específicas en la base de datos que devuelven un subconjunto
 * de los datos de una entidad de película.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public interface ResultMovie {

    /**
     * Obtiene el título de la película.
     *
     * @return el título de la película.
     */
    String getTitle();

    /**
     * Establece el título de la película.
     *
     * @param title el título de la película.
     */
    void setTitle(String title);

    /**
     * Obtiene el año de lanzamiento de la película.
     *
     * @return el año de lanzamiento de la película.
     */
    Integer getReleaseYear();

    /**
     * Establece el año de lanzamiento de la película.
     *
     * @param releaseYear el año de lanzamiento de la película.
     */
    void setReleaseYear(Integer releaseYear);
}