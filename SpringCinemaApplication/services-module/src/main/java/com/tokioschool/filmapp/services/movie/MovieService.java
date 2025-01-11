package com.tokioschool.filmapp.services.movie;

import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.movie.MovieDto;
import com.tokioschool.filmapp.records.SearchMovieRecord;

public interface MovieService {
    int PAGE_DEFAULT = 0;
    int PAGE_SIZE_DEFAULT = 5;

    PageDTO<MovieDto> searchMovie();
    PageDTO<MovieDto> searchMovie(SearchMovieRecord searchMovieRecord);
}
