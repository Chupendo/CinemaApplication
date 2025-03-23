package com.tokioschool.ratingapp.services.impl;

import com.tokioschool.ratingapp.core.exceptions.NotFoundException;
import com.tokioschool.ratingapp.core.exceptions.OperationNotAllowException;
import com.tokioschool.ratingapp.domains.RatingFilm;
import com.tokioschool.ratingapp.dto.ratings.RatingFilmDto;
import com.tokioschool.ratingapp.dto.users.UserDto;
import com.tokioschool.ratingapp.records.RequestRatingFilmDto;
import com.tokioschool.ratingapp.reports.RatingFilmDao;
import com.tokioschool.ratingapp.services.RatingFilmService;
import com.tokioschool.ratingapp.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.LoginException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingFilmServiceImpl implements RatingFilmService {

    private final RatingFilmDao ratingFilmDao;
    private final UserService userService;

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public RatingFilmDto save(@NonNull RequestRatingFilmDto requestRatingFilmDto) {
        if(ratingFilmDao.findRatingFilmByUserIdAndFilmId(requestRatingFilmDto.userId(), requestRatingFilmDto.filmId()).isPresent()){
            throw new OperationNotAllowException("Operation not allow!!");
        }

        RatingFilm ratingFilm = createOrEditPopulation(new RatingFilm(), requestRatingFilmDto);

        return buildRatingFilmDto(ratingFilm);
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public RatingFilmDto update(@NonNull Long id, @NonNull RequestRatingFilmDto requestRatingFilmDto) {
        // verify if the data exists the data
        Optional<RatingFilm> maybeRatingFilm = ratingFilmDao.findRatingFilmByUserIdAndFilmId(requestRatingFilmDto.userId(), requestRatingFilmDto.filmId());

        if(maybeRatingFilm.isPresent() && !id.equals(maybeRatingFilm.get().getId())) {// exits other register
            throw new OperationNotAllowException("Operation not allow!!");
        }

        // if not exist or exist but will be change the score
        RatingFilm ratingFilm = ratingFilmDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Register with id %d not found".formatted(id)));
        ratingFilm = createOrEditPopulation(ratingFilm, requestRatingFilmDto);

        return buildRatingFilmDto(ratingFilm);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")// TODO falta test
    public RatingFilmDto findRatingByFilmAndUserHandler(@NonNull Long filmId, @NonNull String userId) {
        RatingFilm ratingFilm = ratingFilmDao.findRatingFilmByUserIdAndFilmId(userId,filmId)
                .orElseThrow(()->new NotFoundException("Rating Film not found!"));
        return buildRatingFilmDto(ratingFilm);
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void deleteById(@NonNull Long filmId) {
        RatingFilm ratingFilm = ratingFilmDao.findById(filmId).orElseThrow(
                ()-> new NotFoundException("Ratings not found!")
        );

        ratingFilmDao.delete(ratingFilm);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    protected RatingFilm createOrEditPopulation(RatingFilm ratingFilm, RequestRatingFilmDto requestRatingFilmDto) {

        ratingFilm.setFilmId(requestRatingFilmDto.filmId());
        ratingFilm.setUserId(requestRatingFilmDto.userId());
        ratingFilm.setScore(requestRatingFilmDto.score());

        return ratingFilmDao.save(ratingFilm);
    }

    private static RatingFilmDto buildRatingFilmDto(@NonNull RatingFilm ratingFilm) {
        return RatingFilmDto.builder()
                .id(ratingFilm.getId())
                .userId(ratingFilm.getUserId())
                .filmId(ratingFilm.getFilmId())
                .score(ratingFilm.getScore())
                .createAt(ratingFilm.getCreateAt())
                .updatedAt(ratingFilm.getUpdateAt())
                .build();
    }
}
