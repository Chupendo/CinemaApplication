package com.tokioschool.ratings.facade;

import com.tokioschool.filmapp.dto.ratings.RatingFilmDto;

import java.util.Optional;

public interface RatingFacade {

    RatingFilmDto registerRating(RatingFilmDto ratingFilmDto);

    Optional<RatingFilmDto> findRatingByUserIdAndMovieId(String userId, Long movieId);
}
