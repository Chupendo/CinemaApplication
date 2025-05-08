package com.tokioschool.ratings.facade;

import com.tokioschool.filmapp.dto.ratings.RatingFilmDto;
import com.tokioschool.filmapp.records.AverageRating;

import java.util.Optional;

public interface RatingFacade {

    RatingFilmDto registerRating(RatingFilmDto ratingFilmDto);

    Optional<RatingFilmDto> findRatingByUserIdAndMovieId(String userId, Long movieId);

    Optional<AverageRating> findRatingAverageByMovieId(Long movieId);
}
