package com.tokioschool.ratingapp.services;

import com.tokioschool.ratingapp.dto.ratings.RatingFilmDto;
import com.tokioschool.ratingapp.records.RequestRatingFilmDto;
import org.springframework.lang.NonNull;

public interface RatingFilmService {

    RatingFilmDto save(@NonNull RequestRatingFilmDto requestRatingFilmDto);
    RatingFilmDto update(@NonNull Long id, @NonNull RequestRatingFilmDto requestRatingFilmDto);

    RatingFilmDto findRatingByFilmAndUserHandler(@NonNull Long filmId, @NonNull String userId);

    void deleteById(@NonNull Long filmId);
}
