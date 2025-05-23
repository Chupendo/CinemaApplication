package com.tokioschool.filmapp.services.ratings.impl;

import com.tokioschool.core.exception.OperationNotAllowException;
import com.tokioschool.filmapp.domain.RatingFilm;
import com.tokioschool.filmapp.records.RatingResponseFilmDto;
import com.tokioschool.filmapp.records.RequestRatingFilmDto;
import com.tokioschool.filmapp.repositories.RatingFilmDao;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class RatingFilmServiceImplTest {

    @Mock
    private RatingFilmDao ratingFilmDao;

    @InjectMocks
    private RatingFilmServiceImpl ratingFilmService;


    @Test
    void recoverRatingFilms() {
    }

    @Test
    void saveFilmRating_withExitsRegister_shouldReturnOperationNotAllowException() {
        final String userId = "1L";
        final Long filmId = 1L;
        final BigDecimal score = BigDecimal.ONE;
        final RequestRatingFilmDto requestRatingFilmDto = new RequestRatingFilmDto(null,userId,filmId,score);
        final Optional<RatingFilm> optRatingFilm = Optional.of(RatingFilm.builder()
                        .userId(userId)
                        .filmId(filmId)
                        .score(score)
                        .createAt(LocalDateTime.now())
                        .build());

        Mockito.when(ratingFilmDao.findRatingFilmByUserIdAndFilmId(userId,filmId)).thenReturn(
                optRatingFilm
        );


        Assertions.assertThatThrownBy(() -> ratingFilmService.save(requestRatingFilmDto))
                .isInstanceOf(OperationNotAllowException.class)
                .hasMessage("Operation not allow!!");
    }

    @Test
    void saveFilmRating_withNotExitsRegister_shouldOk() {
        final String userId = "1L";
        final Long filmId = 1L;
        final BigDecimal score = BigDecimal.ONE;
        final RequestRatingFilmDto requestRatingFilmDto = new RequestRatingFilmDto(null,userId,filmId,score);
        final Optional<RatingFilm> optRatingFilm = Optional.of(RatingFilm.builder()
                .userId(userId)
                .filmId(filmId)
                .score(score)
                .createAt(LocalDateTime.now())
                .build());

        Mockito.when(ratingFilmDao.findRatingFilmByUserIdAndFilmId(userId,filmId)).thenReturn(Optional.empty() );

        Mockito.when(ratingFilmDao.saveAndFlush(Mockito.any(RatingFilm.class))).thenReturn(
                optRatingFilm.get()
        );

        RatingResponseFilmDto ratingResponseFilmDto = ratingFilmService.save(requestRatingFilmDto);

        Assertions.assertThat(ratingResponseFilmDto).isNotNull();
    }

    @Test
    void update() {
    }

    @Test
    void findRatingByFilmAndUserHandler() {
    }

    @Test
    void deleteByFilmIdAndUserId() {
    }

    @Test
    void averageRatings() {
    }
}