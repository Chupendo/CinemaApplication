package com.tokioschool.filmapp.repositories;

import com.tokioschool.filmapp.domain.ExportFilm;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio para la entidad `ExportFilms`.
 *
 * Esta interfaz extiende `JpaRepository` para proporcionar métodos CRUD
 * básicos para la entidad `ExportFilms`.
 *
 * Anotaciones utilizadas:
 * - `@Repository` (implícita): Marca esta interfaz como un componente Spring
 *   para la gestión de datos.
 *
 * Métodos heredados:
 * - Métodos CRUD como `save`, `findById`, `findAll`, `deleteById`, entre otros.
 *
 * @author andres.rpenuela
 * @vesion 1.0
 */
public interface ExportFilmDao extends JpaRepository<ExportFilm, Long> {

}