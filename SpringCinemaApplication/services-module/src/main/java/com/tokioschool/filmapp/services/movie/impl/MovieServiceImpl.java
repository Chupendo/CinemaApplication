package com.tokioschool.filmapp.services.movie.impl;

import com.tokioschool.core.exception.NotFoundException;
import com.tokioschool.core.exception.ValidacionException;
import com.tokioschool.filmapp.domain.Artist;
import com.tokioschool.filmapp.domain.Movie;
import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.movie.MovieDto;
import com.tokioschool.filmapp.enums.TYPE_ARTIST;
import com.tokioschool.filmapp.records.RangeReleaseYear;
import com.tokioschool.filmapp.records.SearchMovieRecord;
import com.tokioschool.filmapp.repositories.MovieDao;
import com.tokioschool.filmapp.services.artist.ArtistService;
import com.tokioschool.filmapp.services.movie.MovieService;
import com.tokioschool.helpers.UUIDHelper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Implementación del servicio para gestionar películas.
 *
 * Esta clase proporciona métodos para realizar operaciones CRUD y otras
 * funcionalidades relacionadas con la entidad {@link Movie}.
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
public class MovieServiceImpl implements MovieService {

    private final MovieDao movieDao;
    private final ModelMapper modelMapper;
    private final ArtistService artistService;

    /**
     * Busca películas en el sistema aplicando un filtro por defecto. Si el tamaño de página es 0,
     * devuelve todos los elementos filtrados e ignora la paginación.
     *
     * @return Página de resultados de búsqueda de películas en el sistema.
     */
    @Override
    public PageDTO<MovieDto> searchMovie() {
        return searchMovie(null);
    }

    /**
     * Busca películas en el sistema aplicando un filtro de búsqueda. Si el tamaño de página es 0,
     * devuelve todos los elementos filtrados e ignora la paginación.
     *
     * @param searchMovieRecord Filtro a aplicar.
     * @return Página de resultados de búsqueda de películas en el sistema.
     */
    @Override
    public PageDTO<MovieDto> searchMovie(SearchMovieRecord searchMovieRecord) {
        if ( searchMovieRecord == null || searchMovieRecord.page() == null || searchMovieRecord.pageSize() == null) {
            final int pageDefault = Optional.ofNullable( searchMovieRecord )
                    .map(SearchMovieRecord::page)
                    .orElseGet( () -> 0);
            final int pageSizeDefault = Optional.ofNullable( searchMovieRecord )
                    .map(SearchMovieRecord::pageSize)
                    .orElseGet( () -> 5);

            searchMovieRecord = SearchMovieRecord.builder()
                    .page( pageDefault )
                    .pageSize( pageSizeDefault )
                    .build();
        }

        // Filtros
        List<Movie> movies = movieDao.findAll();
        movies = getMoviesFilterByTitle(searchMovieRecord, movies);
        movies = getMoviesFilterByReleaseYear(searchMovieRecord, movies);

        // Construcción de la página de resultados
        int start = searchMovieRecord.page() * searchMovieRecord.pageSize();

        if (start >= movies.size()) {
            int finalPageSize = searchMovieRecord.pageSize() == 0 ? movies.size() : searchMovieRecord.pageSize();
            return PageDTO.<MovieDto>builder()
                    .items(List.of())
                    .pageNumber(searchMovieRecord.page())
                    .pageSize(finalPageSize)
                    .totalPages((int) Math.ceil(movies.size() / (double) finalPageSize))
                    .build();
        } else {
            List<MovieDto> items;

            if (searchMovieRecord.pageSize() == 0) {
                items = getMovieDtoPageDTO(movies, start, movies.size());
            } else {
                int end = Math.min(start + searchMovieRecord.pageSize(), movies.size());
                items = getMovieDtoPageDTO(movies, start, end);
            }

            int finalPageSize = searchMovieRecord.pageSize() == 0 ? items.size() : searchMovieRecord.pageSize();

            return PageDTO.<MovieDto>builder()
                    .items(items)
                    .pageNumber(searchMovieRecord.page())
                    .pageSize(finalPageSize)
                    .totalPages((int) Math.ceil(movies.size() / (double) finalPageSize))
                    .build();
        }
    }

    /**
     * Encuentra una película dado su identificador en el sistema.
     *
     * @param movieId Identificación de la película.
     * @return La película cuyo identificador coincide con el proporcionado.
     * @throws NotFoundException Si no se encuentra una película con el identificador proporcionado.
     */
    @Override
    public MovieDto getMovieById(Long movieId) throws InvalidDataAccessApiUsageException, NotFoundException {
        return movieDao.findById(movieId)
                .map(movie -> modelMapper.map(movie, MovieDto.class))
                .orElseThrow(() -> new NotFoundException("The movie is not found in the system"));
    }

    /**
     * Crea una nueva película en el sistema.
     *
     * @param movieDto Datos a persistir en la base de datos.
     * @return Película registrada en el sistema.
     * @throws InvalidDataAccessApiUsageException Error al persistir los datos.
     */
    @Override
    @Transactional(rollbackFor = {IllegalArgumentException.class, ValidacionException.class})
    public MovieDto createMovie(MovieDto movieDto) throws InvalidDataAccessApiUsageException {
        if (movieDto == null || StringUtils.stripToNull(movieDto.getResourceId()) == null) {
            Map<String, String> errors = Collections.singletonMap("image", "Resource in movie is required");
            throw new ValidacionException("movie don't create", errors);
        }

        Movie movie = createOrUpdateMovie(new Movie(), movieDto);
        return modelMapper.map(movie, MovieDto.class);
    }

    /**
     * Actualiza una película existente.
     *
     * @param movieId  Identificación de la película a actualizar.
     * @param movieDto Datos a actualizar.
     * @return Película con la información actualizada.
     * @throws InvalidDataAccessApiUsageException Error al persistir los datos.
     * @throws NotFoundException Si no se encuentra la película a actualizar en el sistema.
     */
    @Override
    @Transactional(rollbackFor = {IllegalArgumentException.class, ValidacionException.class})
    public MovieDto updateMovie(Long movieId, MovieDto movieDto) throws InvalidDataAccessApiUsageException, NotFoundException {
        Movie movie = movieDao.findById(movieId).orElseThrow(() -> new NotFoundException("Movie with %d don't found. The image maybe be updated".formatted(movieId)));
        movieDao.flush();
        movie = createOrUpdateMovie(movie, movieDto);
        return modelMapper.map(movie, MovieDto.class);
    }

    /**
     * Copia una colección en otra colección de forma determinista, donde su tamaño está en el rango [start, end].
     *
     * @param movies Colección con los datos fuente.
     * @param start  Posición inicial para copiar de la colección fuente.
     * @param end    Posición final para copiar de la colección fuente.
     * @return Una nueva colección con los datos de la colección fuente en el rango [start, end].
     */
    private List<MovieDto> getMovieDtoPageDTO(List<Movie> movies, int start, int end) {
        return IntStream.range(start, end)
                .mapToObj(movies::get)
                .map(movie -> modelMapper.map(movie, MovieDto.class))
                .toList();
    }

    /**
     * Filtra películas por año de lanzamiento. Si es nulo, no aplica filtro.
     *
     * @param searchMovieRecord Instancia con el valor para filtrar.
     * @param movies            Colección a filtrar.
     * @return Colección de películas filtradas por año de lanzamiento.
     */
    private List<Movie> getMoviesFilterByReleaseYear(SearchMovieRecord searchMovieRecord, @NonNull List<Movie> movies) {
        return Optional.ofNullable(searchMovieRecord)
                .map(SearchMovieRecord::rangeReleaseYear)
                .map(rangeReleaseYear ->
                        movies.stream()
                                .filter(movie -> filterByReleaseYear(movie, rangeReleaseYear))
                                .toList())
                .orElseGet(() -> movies);
    }

    /**
     * Filtra películas por título utilizando un filtro de tipo "LIKE". Si es nulo, no aplica filtro.
     *
     * @param searchMovieRecord Instancia con el valor para filtrar.
     * @param movies            Colección a filtrar.
     * @return Colección de películas filtradas por título.
     */
    private List<Movie> getMoviesFilterByTitle(SearchMovieRecord searchMovieRecord, @NonNull List<Movie> movies) {
        return Optional.ofNullable(searchMovieRecord)
                .map(SearchMovieRecord::title)
                .map(StringUtils::stripToNull)
                .map(StringUtils::lowerCase)
                .map(title ->
                        movies.stream()
                                .filter(movie -> movie.getTitle().toLowerCase().contains(title))
                                .toList())
                .orElseGet(() -> movies);
    }

    /**
     * Filtra una película por rango de años de lanzamiento (mínimo y máximo, inclusivo). Si el rango es nulo, no aplica filtro.
     *
     * @param movie             Película a filtrar.
     * @param rangeReleaseYear  Rango mínimo y máximo de años para filtrar.
     * @return true si el rango es nulo o el año de lanzamiento de la película está dentro del rango, de lo contrario false.
     */
    private boolean filterByReleaseYear(@NonNull Movie movie, @Nullable RangeReleaseYear rangeReleaseYear) {
        if (rangeReleaseYear == null || movie.getReleaseYear() == null) {
            return true;
        } else {
            if (rangeReleaseYear.yearMin() == null || rangeReleaseYear.yearMin() <= 0) {
                if (rangeReleaseYear.yearMax() == null || rangeReleaseYear.yearMax() <= 0) {
                    return true;
                } else {
                    return movie.getReleaseYear() <= rangeReleaseYear.yearMax();
                }
            } else {
                if (rangeReleaseYear.yearMax() == null || rangeReleaseYear.yearMax() <= 0) {
                    return movie.getReleaseYear() >= rangeReleaseYear.yearMin();
                } else {
                    return movie.getReleaseYear() >= rangeReleaseYear.yearMin()
                            && movie.getReleaseYear() <= rangeReleaseYear.yearMax();
                }
            }
        }
    }

    /**
     * Crea o actualiza una película en el sistema.
     *
     * @param movie    Película a actualizar. Si está vacía, crea una nueva película.
     * @param movieDto Datos para actualizar o crear la película.
     * @return La película actualizada o creada.
     * @throws NotFoundException Si los datos de la película son nulos.
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = NotFoundException.class)
    protected Movie createOrUpdateMovie(Movie movie, MovieDto movieDto) throws NotFoundException {
        if (movieDto == null) {
            throw new IllegalArgumentException("The data of movie is null");
        }
        movie.setTitle(movieDto.getTitle());

        // Obtener manager del sistema con validación
        Artist manager = Optional.of(movieDto.getManagerDto())
                .map(ArtistDto::getId)
                .map(this::getArtistById)
                .filter(artist -> artist.getTypeArtist().equals(TYPE_ARTIST.DIRECTOR))
                .orElseThrow(() -> new NotFoundException("The manager is strong"));
        movie.setManager(manager);

        // Obtener lista de artistas
        List<Artist> artists = movieDto.getArtistDtos().stream()
                .map(artistDto -> getArtistById(artistDto.getId()))
                .collect(Collectors.toList());

        // Validación
        if (artists.stream().anyMatch(artist -> artist.getTypeArtist().equals(TYPE_ARTIST.DIRECTOR))) {
            throw new NotFoundException("The artist list is strong");
        }
        movie.setArtists(artists);

        movie.setReleaseYear(movieDto.getReleaseYear());

        final Optional<UUID> maybeUUID = UUIDHelper.mapStringToUUID(movieDto.getResourceId());
        maybeUUID.ifPresent(movie::setImage);

        return movieDao.saveAndFlush(movie);
    }

    /**
     * Encuentra un artista dado su identificador.
     *
     * @param artistId Identificación del artista.
     * @return El artista con el identificador proporcionado.
     * @throws NotFoundException Si no se encuentra el artista con el identificador proporcionado.
     */
    @Transactional(readOnly = true)
    protected Artist getArtistById(Long artistId) throws NotFoundException {
        final ArtistDto artistDto = artistService.findById(artistId);
        return Artist.builder()
                .id(artistDto.getId())
                .name(artistDto.getName())
                .surname(artistDto.getSurname())
                .typeArtist(TYPE_ARTIST.valueOf(artistDto.getTypeArtist()))
                .build();
    }
}