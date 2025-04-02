package com.tokioschool.ratingapp.services.impl;

import com.tokioschool.ratingapp.core.exceptions.NotFoundException;
import com.tokioschool.ratingapp.core.exceptions.OperationNotAllowException;
import com.tokioschool.ratingapp.domains.RatingFilm;
import com.tokioschool.ratingapp.dtos.AverageRating;
import com.tokioschool.ratingapp.dtos.RatingFilmDto;
import com.tokioschool.ratingapp.dtos.RatingResponseFilmDto;
import com.tokioschool.ratingapp.dtos.RequestRatingFilmDto;
import com.tokioschool.ratingapp.repositories.RatingFilmDao;
import com.tokioschool.ratingapp.services.RatingFilmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingFilmServiceImpl implements RatingFilmService {

    private final RatingFilmDao ratingFilmDao;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public List<RatingFilmDto> recoverRatingFilms() {
        return ratingFilmDao.findAll().stream().map(RatingFilmServiceImpl::buildRatingFilmDto).toList();
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public RatingResponseFilmDto save(@NonNull RequestRatingFilmDto requestRatingFilmDto) throws IncorrectResultSizeDataAccessException {
        if(ratingFilmDao.findRatingFilmByUserIdAndFilmId(requestRatingFilmDto.userId(), requestRatingFilmDto.filmId()).isPresent()){
            throw new OperationNotAllowException("Operation not allow!!");
        }

        RatingFilm ratingFilm = createOrEditPopulation(new RatingFilm(), requestRatingFilmDto);

        return new RatingResponseFilmDto( ratingFilm.getScore(),ratingFilm.getCreateAt() );
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public RatingFilmDto update(@NonNull Long filmId, @NonNull Long userId, @NonNull RequestRatingFilmDto requestRatingFilmDto) throws IncorrectResultSizeDataAccessException {
        // Busca el registro existente
        RatingFilm ratingFilm = ratingFilmDao.findRatingFilmByUserIdAndFilmId(userId, filmId)
                .orElseThrow(() -> new NotFoundException("Register with film id %d and user id %d not found".formatted(filmId, userId)));

        // Si el registro esta presente y no cambia la calificacion, lanza una excepción
        if ( ratingFilmDao.findRatingFilmByUserIdAndFilmId(requestRatingFilmDto.userId(), requestRatingFilmDto.filmId() ).isPresent()
        && ratingFilm.getScore().setScale(2, RoundingMode.UNNECESSARY).equals(
                requestRatingFilmDto.score().setScale(2, RoundingMode.UNNECESSARY))
        ){
            throw new OperationNotAllowException("Operation not allowed: duplicate full register or duplicate rating!!");
        }

        // Si el usuario y la pelicula a actualizar, cambia y los nuevos valores existe, lanza una excepción
        if (!filmId.equals(requestRatingFilmDto.filmId()) && !userId.equals(requestRatingFilmDto.userId()) &&
                ratingFilmDao.findRatingFilmByUserIdAndFilmId(requestRatingFilmDto.userId(), requestRatingFilmDto.filmId()).isPresent()) {
            throw new OperationNotAllowException("Operation not allowed: duplicate register!");
        }

        // Si la pelicula cambia, y el usuario ya tiene una puntuacion
        if (!filmId.equals(requestRatingFilmDto.filmId()) &&
                ratingFilmDao.findRatingFilmByUserIdAndFilmId(requestRatingFilmDto.userId(), requestRatingFilmDto.filmId()).isPresent()) {
            throw new OperationNotAllowException("Operation not allowed: film with other rating!");
        }

        // Si el usuario cambia, y la pelicual ya tiene una puntacon
        if (!userId.equals(requestRatingFilmDto.userId()) &&
                ratingFilmDao.findRatingFilmByUserIdAndFilmId(requestRatingFilmDto.userId(), requestRatingFilmDto.filmId()).isPresent()) {
            throw new OperationNotAllowException("Operation not allowed: user with other rating!");
        }


        // Actualiza el registro con la nueva calificación
        ratingFilm = createOrEditPopulation(ratingFilm, requestRatingFilmDto);
        return buildRatingFilmDto(ratingFilm);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public RatingFilmDto findRatingByFilmAndUserHandler(@NonNull Long filmId, @NonNull Long userId) throws IncorrectResultSizeDataAccessException {
        RatingFilm ratingFilm = ratingFilmDao.findRatingFilmByUserIdAndFilmId(userId,filmId)
                .orElseThrow(()->new NotFoundException("Rating Film not found!"));
        return buildRatingFilmDto(ratingFilm);
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void deleteByFilmIdAndUserId(@NonNull Long filmId,@NonNull Long userId) throws IncorrectResultSizeDataAccessException {
        RatingFilm ratingFilm = ratingFilmDao.findRatingFilmByUserIdAndFilmId(userId,filmId).orElseThrow(
                ()-> new NotFoundException("Ratings not found!")
        );

        ratingFilmDao.delete(ratingFilm);
    }

    @Override
    public AverageRating averageRatings(Long filmId) throws NotFoundException{
        return ratingFilmDao.mainScoreByFilmId(filmId).orElseThrow(() -> new NotFoundException("There aren't account by film"));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    protected RatingFilm createOrEditPopulation(RatingFilm ratingFilm, RequestRatingFilmDto requestRatingFilmDto) {

        ratingFilm.setFilmId(requestRatingFilmDto.filmId());
        ratingFilm.setUserId(requestRatingFilmDto.userId());
        ratingFilm.setScore(requestRatingFilmDto.score());

        if(ratingFilm.getId() != null ){
            ratingFilm.setUpdateAt(LocalDateTime.now());
        }

        return ratingFilmDao.saveAndFlush(ratingFilm);
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
