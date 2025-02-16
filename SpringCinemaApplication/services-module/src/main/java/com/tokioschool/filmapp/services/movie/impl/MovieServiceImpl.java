package com.tokioschool.filmapp.services.movie.impl;

import com.tokioschool.core.exception.NotFoundException;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Operations for give support to requests about movies
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
     * Search movie in the system apply a filter by default, also if page size is 0,
     * then return all items filters and ignored the page or page number.
     *
     * @return page of result of search of movies in the system
     */
    @Override
    public PageDTO<MovieDto> searchMovie() {
        return searchMovie(null);
    }

    /**
     * Search movie in the system apply a filter of searched, also if page size is 0,
     * then return all items filters and ignored the page or page number.
     *
     * @param searchMovieRecord filter to apply
     * @return page of result of search of movies in the system
     */
    @Override
    public PageDTO<MovieDto> searchMovie(SearchMovieRecord searchMovieRecord){
        if(searchMovieRecord == null) {
            searchMovieRecord = SearchMovieRecord.builder()
                    .page(0)
                    .pageSize(5)
                    .build();
        }

        // filters
        List<Movie> movies = movieDao.findAll();
        movies = getMoviesFilterByTitle(searchMovieRecord,movies);
        movies = getMoviesFilterByReleaseYear(searchMovieRecord, movies);

        // Build Page<MovieResult>
        int star = searchMovieRecord.page() * searchMovieRecord.pageSize(); // first item of Page<T>

        if(star >= movies.size()) { // there aren't items to show
            int finalPageSize = searchMovieRecord.pageSize() == 0? movies.size() : searchMovieRecord.pageSize();
            return PageDTO.<MovieDto>builder()
                    .items(List.of())
                    .pageNumber(searchMovieRecord.page())
                    .pageSize(finalPageSize )
                    .totalPages((int) Math.ceil(movies.size()/(double)finalPageSize ))
                    .build();

        }else{
            List<MovieDto> items;

            if(searchMovieRecord.pageSize() ==  0){
                 items = getMovieDtoPageDTO(movies, star, movies.size());
            }else{
                int end = Math.min(star + searchMovieRecord.pageSize(),movies.size());
                items = getMovieDtoPageDTO(movies, star, end);
            }

            int finalPageSize = searchMovieRecord.pageSize() == 0? items.size() : searchMovieRecord.pageSize();

            return PageDTO.<MovieDto>builder()
                    .items(items)
                    .pageNumber(searchMovieRecord.page())
                    .pageSize( finalPageSize )
                    .totalPages((int) Math.ceil(movies.size() /(double) finalPageSize ) )
                    .build();

        }
    }

    /**
     * Find the movie given it's identifier in the system
     *
     * @param movieId identification of the movie
     * @return the movie which identification in the system is the given
     * @throws NotFoundException if there aren't a movie with this identification
     *  @throws NotFoundException if the id given is null
     */
    @Override
    public MovieDto getMovieById(Long movieId) throws InvalidDataAccessApiUsageException,NotFoundException {
        return movieDao.findById(movieId)
                .map(movie -> modelMapper.map(movie, MovieDto.class))
                .orElseThrow(() -> new NotFoundException("The movie is not found in the system"));
    }

    /**
     * Created a new Movie in the system
     * @param movieDto data to persists in bbdd
     * @return movie register in the system
     *
     * @throws InvalidDataAccessApiUsageException error to persist the data
     */
    @Override
    @Transactional(rollbackFor = IllegalArgumentException.class)
    public MovieDto createMovie(MovieDto movieDto) throws InvalidDataAccessApiUsageException {
        Movie movie = createOrUpdateMovie(new Movie(),movieDto);
        return modelMapper.map(movie, MovieDto.class);
    }

    /**
     * Updated movie
     * @param movieId identificaiton of movie to updated
     * @param movieDto data to updated
     * @return movie with updated information
     *
     * @throws InvalidDataAccessApiUsageException error to persist the data
     * @throws NotFoundException if the movie to updated is don't found in the system
     */
    @Override
    public MovieDto updateMovie(Long movieId, MovieDto movieDto) throws InvalidDataAccessApiUsageException, NotFoundException {
        Movie movie = movieDao.findById(movieId).orElseThrow(() -> new NotFoundException("Movie with %d don't found. The image maybe be updated".formatted(movieId)));
        movieDao.flush();
        movie = createOrUpdateMovie( movie,movieDto);
        return modelMapper.map(movie, MovieDto.class);
    }

    /**
     * Copy a collection in other collection deterministic where your size is range
     * between [star,end]
     *
     * @param movies collection with data source
     * @param star position where start to copy the source collection
     * @param end position where finished of copy the source collection
     * @return a new collection with tha data of collection given, and are hosting in the position [star,end]
     */
    private List<MovieDto> getMovieDtoPageDTO(List<Movie> movies, int star, int end ) {
        return IntStream.range(star, end)
                .mapToObj(movies::get)
                .map(movie -> modelMapper.map(movie, MovieDto.class))
                .toList();


    }

    /**
     * Filter a movie by release year, if is null then don't filter
     *
     * @param searchMovieRecord instance with the value for filter
     * @param movies collection to filter
     * @return collection of movies to check your release year field with of the filter
     *
     * {@link #filterByReleaseYear(Movie, RangeReleaseYear) Dependency}
     */
    private List<Movie> getMoviesFilterByReleaseYear(SearchMovieRecord searchMovieRecord,@NonNull List<Movie> movies) {
        return Optional.ofNullable(searchMovieRecord)
                .map(SearchMovieRecord::rangeReleaseYear)
                .map(rangeReleaseYear ->
                        movies
                                .stream()
                                .filter(movie -> filterByReleaseYear(movie, rangeReleaseYear))
                                .toList())
                .orElseGet(()->movies);
    }

    /**
     * Filter type "LIKE" a movie by title, if is null then don't filter
     *
     * @param searchMovieRecord instance with the value for filter
     * @param movies collection to filter
     * @return collection of movies to check your title field with of the filter
     */
    private List<Movie> getMoviesFilterByTitle(SearchMovieRecord searchMovieRecord,@NonNull List<Movie> movies) {
        return Optional.ofNullable(searchMovieRecord)
                .map(SearchMovieRecord::title)
                .map(StringUtils::stripToNull)
                .map(StringUtils::lowerCase)
                .map(title ->
                        movies
                                .stream()
                                .filter(movie -> movie.getTitle().toLowerCase().contains(title))
                                .toList())
                .orElseGet(() -> movies);
    }

    /**
     * Filter a movie by range release years min and max inclusive, if the range is null, then
     * don't filter and result is true
     *
     * Pre-Conditions:
     * - If rangeReleaseYear is null, then don't filter, return true
     * - If rangeReleaseYear.min <=0, then there aren't min.
     * - If rangeReleaseYear.max <=0, then there aren't max.
     *
     * @param movie list of movies to filter by release year field
     * @param rangeReleaseYear range min and max of year to filter
     * @return true if the range is null or the field release year of the movie is between to range, otherwise false
     *
     */
    private boolean filterByReleaseYear(@NonNull Movie movie,@Nullable RangeReleaseYear rangeReleaseYear) {
        if(rangeReleaseYear==null || movie.getReleaseYear() == null){
            return true;
        }else{
            if(rangeReleaseYear.yearMin() == null || rangeReleaseYear.yearMin() <=0 ){
                // no hay minimo
                if(rangeReleaseYear.yearMax() == null || rangeReleaseYear.yearMax() <= 0 ){
                    // ni hay ni minimo, ni maximo
                    return true;
                }else{
                    // no hay minimo, pero si maximo
                    return movie.getReleaseYear() <= rangeReleaseYear.yearMax();
                }
            }else{
                // hay minimo
                if(rangeReleaseYear.yearMax() == null || rangeReleaseYear.yearMax() <= 0 ){
                    // hay ni minimo, pero no maximo
                    return movie.getReleaseYear() >= rangeReleaseYear.yearMin();
                }else{
                    // hay minimo y maximo
                    return movie.getReleaseYear() >= rangeReleaseYear.yearMin()
                            && movie.getReleaseYear() >= rangeReleaseYear.yearMax();
                }
            }
        }

    }

    /**
     * Create or update a movie in the system
     *
     * @param movie movie to update, if is empty then create a new movie, otherwise update the movie
     * @param movieDto data to update or create the movie
     * @return the movie updated or created
     *
     * @throws NotFoundException if the data of movie is null
     */

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = NotFoundException.class)
    protected Movie createOrUpdateMovie(Movie movie, MovieDto movieDto) throws NotFoundException{
        if(movieDto == null){
            throw new IllegalArgumentException("The data of movie is null");
        }
        movie.setTitle(movieDto.getTitle());

        movie.setManager(getArtistById(movieDto.getManagerDto().getId()));

        // collection mutable
        List<Artist> artists = movieDto.getArtistDtos().stream()
                .map(artistDto -> getArtistById(artistDto.getId()))
                .collect(Collectors.toList());
        movie.setArtists(artists);

        movie.setReleaseYear(movieDto.getReleaseYear());

        final Optional<UUID> maybeUUID = UUIDHelper.mapStringToUUID( movieDto.getResourceId() );
        maybeUUID.ifPresent(movie::setImage);

        return movieDao.saveAndFlush(movie);
    }

    /**
     * Find the Artist given you id
     *
     * @see {@link ArtistService::findById(Long) }
     *
     * @param artistId identification of artist
     * @return the artis with the identification given
     * @throws NotFoundException if the artis with id given is null
     */
    @Transactional(readOnly = true)
    protected Artist getArtistById(Long artistId) throws NotFoundException {
        final ArtistDto artistDto = artistService.findById(artistId);
        final Artist artist = Artist.builder()
                .id(artistDto.getId())
                .name(artistDto.getName())
                .surname(artistDto.getSurname())
                .typeArtist(TYPE_ARTIST.valueOf(artistDto.getTypeArtist()))
                .build();
        return artist;
    }
}
