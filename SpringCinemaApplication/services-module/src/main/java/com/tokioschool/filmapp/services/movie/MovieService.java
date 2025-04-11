package com.tokioschool.filmapp.services.movie;

import com.tokioschool.core.exception.NotFoundException;
import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.movie.MovieDto;
import com.tokioschool.filmapp.records.SearchMovieRecord;
import org.springframework.dao.InvalidDataAccessApiUsageException;

/**
 * Interfaz para el servicio de gestión de películas.
 *
 * Esta interfaz define los métodos necesarios para realizar operaciones relacionadas
 * con la entidad Movie, como buscar películas, obtener una película por su ID,
 * crear una nueva película y actualizar una existente.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public interface MovieService {
    /**
     * Número de página predeterminado para la paginación.
     */
    int PAGE_DEFAULT = 0;

    /**
     * Tamaño de página predeterminado para la paginación.
     */
    int PAGE_SIZE_DEFAULT = 5;

    /**
     * Busca películas en el sistema aplicando un filtro por defecto.
     *
     * @return Un objeto {@link PageDTO} que contiene una lista de películas en formato DTO.
     */
    PageDTO<MovieDto> searchMovie();

    /**
     * Busca películas en el sistema aplicando un filtro de búsqueda.
     *
     * @param searchMovieRecord Objeto que contiene los criterios de búsqueda.
     * @return Un objeto {@link PageDTO} que contiene una lista de películas en formato DTO.
     */
    PageDTO<MovieDto> searchMovie(SearchMovieRecord searchMovieRecord);

    /**
     * Obtiene una película por su identificador único.
     *
     * @param movieId El ID de la película a buscar.
     * @return Un objeto {@link MovieDto} que representa la película encontrada.
     * @throws InvalidDataAccessApiUsageException Si ocurre un error al acceder a los datos.
     * @throws NotFoundException Si no se encuentra una película con el ID proporcionado.
     */
    MovieDto getMovieById(Long movieId) throws InvalidDataAccessApiUsageException, NotFoundException;

    /**
     * Crea una nueva película en el sistema.
     *
     * @param movieDto Objeto que contiene los datos de la película a crear.
     * @return Un objeto {@link MovieDto} que representa la película creada.
     * @throws InvalidDataAccessApiUsageException Si ocurre un error al persistir los datos.
     */
    MovieDto createMovie(MovieDto movieDto) throws InvalidDataAccessApiUsageException;

    /**
     * Actualiza una película existente en el sistema.
     *
     * @param movieId El ID de la película a actualizar.
     * @param movieDto Objeto que contiene los datos actualizados de la película.
     * @return Un objeto {@link MovieDto} que representa la película actualizada.
     * @throws InvalidDataAccessApiUsageException Si ocurre un error al persistir los datos.
     * @throws NotFoundException Si no se encuentra una película con el ID proporcionado.
     */
    MovieDto updateMovie(Long movieId, MovieDto movieDto) throws InvalidDataAccessApiUsageException, NotFoundException;
}