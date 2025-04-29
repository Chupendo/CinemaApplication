package com.tokioschool.filmapp.services.artist;

import com.tokioschool.core.exception.NotFoundException;
import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.movie.MovieDto;
import com.tokioschool.filmapp.records.SearchArtistRecord;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * Interfaz para el servicio de gestión de artistas.
 *
 * Esta interfaz define los métodos necesarios para realizar operaciones relacionadas
 * con la entidad Artist, como obtener todos los artistas, registrar un nuevo artista
 * y buscar un artista por su ID.
 */
public interface ArtistService {

    /**
     * Obtiene una lista de todos los artistas en formato DTO.
     *
     * @return Una lista de objetos {@link ArtistDto} que representan a los artistas.
     */
    List<ArtistDto> findByAll();

    PageDTO<ArtistDto> searchArtist(int pageNumber, int pageSize, SearchArtistRecord searchArtistRecord);
    /**
     * Registra un nuevo artista en la base de datos.
     *
     * @param artistDto El objeto {@link ArtistDto} con los datos del artista a registrar.
     * @return El objeto {@link ArtistDto} del artista registrado.
     * @throws IllegalArgumentException Si los datos proporcionados no son válidos.
     */
    ArtistDto registerArtist(ArtistDto artistDto) throws IllegalArgumentException;

    ArtistDto updatedArtist(@NonNull Long artistId, @NonNull ArtistDto artistDto);
    /**
     * Busca un artista por su ID.
     *
     * @param artistId El ID del artista a buscar.
     * @return El objeto {@link ArtistDto} que representa al artista encontrado.
     * @throws NotFoundException Si no se encuentra un artista con el ID proporcionado.
     */
    ArtistDto findById(Long artistId) throws NotFoundException;

    List<MovieDto> findMoviesByManagerById(@NonNull Long managerId);
}