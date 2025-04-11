package com.tokioschool.filmapp.services.artist.impl;

import com.tokioschool.core.exception.NotFoundException;
import com.tokioschool.filmapp.domain.Artist;
import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.enums.TYPE_ARTIST;
import com.tokioschool.filmapp.repositories.ArtistDao;
import com.tokioschool.filmapp.services.artist.ArtistService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio para gestionar artistas.
 *
 * Esta clase proporciona métodos para realizar operaciones CRUD y otras
 * funcionalidades relacionadas con la entidad {@link Artist}.
 *
 * Anotaciones:
 * - {@link Service}: Marca esta clase como un componente de servicio de Spring.
 * - {@link RequiredArgsConstructor}: Genera un constructor con los campos finales requeridos.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final ArtistDao artistDao;
    private final ModelMapper modelMapper;

    /**
     * Obtiene una lista de todos los artistas en formato DTO.
     *
     * @return Una lista de objetos {@link ArtistDto} que representan a los artistas.
     */
    @Override
    public List<ArtistDto> findByAll() {
        return artistDao.findAll().stream().map(artist -> modelMapper.map(artist, ArtistDto.class)).toList();
    }

    /**
     * Registra un nuevo artista en la base de datos.
     *
     * @param artistDto El objeto {@link ArtistDto} con los datos del artista a registrar.
     * @return El objeto {@link ArtistDto} del artista registrado.
     * @throws IllegalArgumentException Si los datos proporcionados no son válidos.
     */
    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ArtistDto registerArtist(ArtistDto artistDto) throws IllegalArgumentException {
        Artist artist = Artist.builder().build();
        return populationCreateOrEditArtist(artist, artistDto);
    }

    /**
     * Busca un artista por su ID.
     *
     * @param artistId El ID del artista a buscar.
     * @return El objeto {@link ArtistDto} que representa al artista encontrado.
     * @throws NotFoundException Si no se encuentra un artista con el ID proporcionado.
     */
    @Override
    public ArtistDto findById(Long artistId) throws NotFoundException {
        return Optional.ofNullable(artistId)
                .map(artistDao::findById)
                .map(artist -> modelMapper.map(artist, ArtistDto.class))
                .orElseThrow(() -> new NotFoundException("Artist with id: %d not found".formatted(artistId)));
    }

    /**
     * Población y guardado de un artista en la base de datos.
     *
     * @param artist    El objeto {@link Artist} a crear o editar.
     * @param artistDto El objeto {@link ArtistDto} con los datos del artista.
     * @return El objeto {@link ArtistDto} del artista creado o editado.
     * @throws IllegalArgumentException Si los datos proporcionados no son válidos.
     */
    protected ArtistDto populationCreateOrEditArtist(Artist artist, ArtistDto artistDto) throws IllegalArgumentException {
        artist.setName(artistDto.getName());
        artist.setSurname(artistDto.getSurname());
        artist.setTypeArtist(TYPE_ARTIST.valueOf(artistDto.getTypeArtist().toUpperCase()));

        artist = artistDao.save(artist);

        return modelMapper.map(artist, ArtistDto.class);
    }
}