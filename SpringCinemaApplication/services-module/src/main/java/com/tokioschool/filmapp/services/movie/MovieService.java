package com.tokioschool.filmapp.services.movie;

import com.tokioschool.core.exception.NotFoundException;
import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.movie.MovieDto;
import com.tokioschool.filmapp.records.SearchMovieRecord;
import org.springframework.dao.InvalidDataAccessApiUsageException;

public interface MovieService {
    int PAGE_DEFAULT = 0;
    int PAGE_SIZE_DEFAULT = 5;

    PageDTO<MovieDto> searchMovie();
    PageDTO<MovieDto> searchMovie(SearchMovieRecord searchMovieRecord);

    MovieDto getMovieById(Long movieId) throws InvalidDataAccessApiUsageException,NotFoundException;

    MovieDto createMovie(MovieDto movieDto) throws InvalidDataAccessApiUsageException;
    MovieDto updateMovie(Long movieId, MovieDto movieDto) throws InvalidDataAccessApiUsageException,NotFoundException;
}
