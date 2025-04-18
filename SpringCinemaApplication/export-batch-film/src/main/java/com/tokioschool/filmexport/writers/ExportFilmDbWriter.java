package com.tokioschool.filmexport.writers;

import com.tokioschool.filmapp.domain.ExportFilm;
import com.tokioschool.filmapp.repositories.ExportFilmDao;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Escritor de elementos para guardar objetos de tipo `ExportFilm` en la base de datos.
 *
 * Esta clase implementa la interfaz `ItemWriter` de Spring Batch, permitiendo
 * escribir un conjunto de elementos (`Chunk`) en un repositorio de datos.
 *
 * Anotaciones utilizadas:
 * - `@Component`: Marca esta clase como un componente Spring para la inyección de dependencias.
 * - `@StepScope`: Define el alcance del bean como específico para un paso de Spring Batch.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Component
@StepScope
public class ExportFilmDbWriter implements ItemWriter<ExportFilm> {

    /** DAO para acceder y guardar los objetos `ExportFilm` en la base de datos. */
    private final ExportFilmDao exportFilmDao;

    /**
     * Constructor que inyecta el DAO de `ExportFilm`.
     *
     * @param exportFilmDao DAO para la gestión de los objetos `ExportFilm`.
     */
    public ExportFilmDbWriter(ExportFilmDao exportFilmDao) {
        this.exportFilmDao = exportFilmDao;
    }

    /**
     * Escribe un conjunto de objetos `ExportFilm` en la base de datos.
     *
     * @param exportFilms Conjunto de elementos a guardar.
     */
    @Override
    public void write( @NonNull Chunk<? extends ExportFilm> exportFilms) {
        exportFilmDao.saveAll(exportFilms);
    }
}