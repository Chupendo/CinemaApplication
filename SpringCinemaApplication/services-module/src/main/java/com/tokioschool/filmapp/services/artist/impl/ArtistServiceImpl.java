package com.tokioschool.filmapp.services.artist.impl;

import com.tokioschool.core.exception.NotFoundException;
import com.tokioschool.core.exception.OperationNotAllowException;
import com.tokioschool.filmapp.domain.Artist;
import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.movie.MovieDto;
import com.tokioschool.filmapp.dto.user.UserDto;
import com.tokioschool.filmapp.enums.TYPE_ARTIST;
import com.tokioschool.filmapp.records.SearchArtistRecord;
import com.tokioschool.filmapp.repositories.ArtistDao;
import com.tokioschool.filmapp.repositories.MovieDao;
import com.tokioschool.filmapp.services.artist.ArtistService;
import com.tokioschool.filmapp.services.movie.MovieService;
import com.tokioschool.filmapp.specifications.ArtistSpecification;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private final MovieDao movieDao;

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

    @Override
    public PageDTO<ArtistDto> searchArtist(int pageNumber, int pageSize, SearchArtistRecord searchArtistRecord) {
        Specification<Artist> spec = Specification.allOf();

        if (searchArtistRecord != null) {
            spec = Specification
                    .where(ArtistSpecification.containsName(searchArtistRecord.name()))
                    .and(ArtistSpecification.containsSurname(searchArtistRecord.surname()))
                    .and(ArtistSpecification.hasTypeArtist(searchArtistRecord.type()));
        }

        List<ArtistDto> artistDtos = artistDao.findAll(spec)
                .stream()
                .map(artist -> modelMapper.map(artist, ArtistDto.class))
                .toList();

        int startItem = pageNumber * pageSize;
        final int totalPages = pageSize == NumberUtils.SHORT_ZERO ? NumberUtils.SHORT_ONE : (int) Math.ceil((artistDtos.size() / (double) pageSize));


        if (startItem >= artistDtos.size()) {
            return PageDTO.<ArtistDto>builder()
                    .items(List.of())
                    .pageSize(pageSize)
                    .pageNumber(pageNumber)
                    .totalPages(totalPages)
                    .build();
        } else {
            if (pageSize == NumberUtils.SHORT_ZERO) {
                artistDtos = (List<ArtistDto>) getItemsPageDto(artistDtos, startItem, artistDtos.size());
            } else {
                int end = Math.min(startItem + pageSize, artistDtos.size());
                artistDtos = (List<ArtistDto>) getItemsPageDto(artistDtos, startItem, end);
            }
        }

        return PageDTO.<ArtistDto>builder()
                .items(artistDtos)
                .pageSize(pageSize)
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .build();
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

    @Override
    public ArtistDto updatedArtist(@NonNull Long artistId,@NonNull ArtistDto artistDto) {
        if(!findMoviesByManagerById(artistId).isEmpty()){
            throw new OperationNotAllowException("This manager has been used");
        }
        final Artist artist = Optional.of(artistId)
                .map(artistDao::findById)
                .filter(Optional::isPresent)
                .get()
                .orElseThrow(() -> new UsernameNotFoundException("Artist no't found"));

        return populationCreateOrEditArtist(artist, artistDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> findMoviesByManagerById(@NonNull Long managerId) {
        return movieDao.findMovieByManagerId(managerId)
                .stream()
                .map(movie -> modelMapper.map(movie,MovieDto.class))
                .toList();
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

    @Override
    public List<ArtistDto> findByAllByTypeArtist(@NonNull TYPE_ARTIST typeArtist) {
        return artistDao.findByTypeArtistIs(typeArtist)
                .stream()
                .map(artist -> modelMapper.map(artist, ArtistDto.class))
                .toList();
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

    /**
     * Obtiene una sublista de elementos para la paginación.
     *
     * @param items Lista de elementos.
     * @param start Índice inicial.
     * @param end Índice final.
     * @return Sublista de elementos.
     */
    private static List<?> getItemsPageDto(List<?> items, int start, int end) {
        return IntStream.range(start, end)
                .mapToObj(items::get)
                .toList();
    }
}