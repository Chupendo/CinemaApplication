package com.tokioschool.filmapp.services.movie.impl;

import com.tokioschool.filmapp.domain.Movie;
import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.movie.MovieDto;
import com.tokioschool.filmapp.records.RangeReleaseYear;
import com.tokioschool.filmapp.records.SearchMovieRecord;
import com.tokioschool.filmapp.repositories.MovieDao;
import com.tokioschool.filmapp.services.movie.MovieService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
}
