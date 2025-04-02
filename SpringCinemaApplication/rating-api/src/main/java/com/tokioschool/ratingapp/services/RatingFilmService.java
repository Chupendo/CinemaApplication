package com.tokioschool.ratingapp.services;

import com.tokioschool.ratingapp.core.exceptions.NotFoundException;
import com.tokioschool.ratingapp.dtos.AverageRating;
import com.tokioschool.ratingapp.dtos.RatingFilmDto;
import com.tokioschool.ratingapp.dtos.RatingResponseFilmDto;
import com.tokioschool.ratingapp.dtos.RequestRatingFilmDto;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.lang.NonNull;

import java.util.List;

public interface RatingFilmService {

    List<RatingFilmDto> recoverRatingFilms();

    RatingResponseFilmDto save(@NonNull RequestRatingFilmDto requestRatingFilmDto) throws IncorrectResultSizeDataAccessException;
    RatingFilmDto update(@NonNull Long filmId,@NonNull Long userId, @NonNull RequestRatingFilmDto requestRatingFilmDto) throws IncorrectResultSizeDataAccessException;

    RatingFilmDto findRatingByFilmAndUserHandler(@NonNull Long filmId, @NonNull Long userId) throws IncorrectResultSizeDataAccessException;

    void deleteByFilmIdAndUserId(@NonNull Long filmId,@NonNull Long UserId) throws IncorrectResultSizeDataAccessException;

    AverageRating averageRatings(Long filmId) throws NotFoundException;


}
