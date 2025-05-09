package com.tokioschool.filmapp.services.ratings.impl;

import com.tokioschool.core.exception.NotFoundException;
import com.tokioschool.core.exception.OperationNotAllowException;
import com.tokioschool.filmapp.domain.RatingFilm;
import com.tokioschool.filmapp.dto.ratings.RatingFilmDto;
import com.tokioschool.filmapp.records.AverageRating;
import com.tokioschool.filmapp.records.RatingResponseFilmDto;
import com.tokioschool.filmapp.records.RequestRatingFilmDto;
import com.tokioschool.filmapp.repositories.RatingFilmDao;
import com.tokioschool.filmapp.services.ratings.RatingFilmService;
import com.tokioschool.helpers.DateHelper;
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

/**
 * Implementación del servicio para gestionar las calificaciones de películas.
 *
 * Esta clase proporciona métodos para realizar operaciones CRUD y otras
 * funcionalidades relacionadas con la entidad {@link RatingFilm}.
 *
 * Anotaciones:
 * - {@link Service}: Marca esta clase como un componente de servicio de Spring.
 * - {@link RequiredArgsConstructor}: Genera un constructor con los campos finales requeridos.
 * - {@link Slf4j}: Proporciona un logger para la clase.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RatingFilmServiceImpl implements RatingFilmService {

    private final RatingFilmDao ratingFilmDao;

    /**
     * Recupera todas las calificaciones de películas.
     *
     * @return Una lista de objetos {@link RatingFilmDto} que representan las calificaciones.
     */
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public List<RatingFilmDto> recoverRatingFilms() {
        return ratingFilmDao.findAll().stream().map(RatingFilmServiceImpl::buildRatingFilmDto).toList();
    }

    /**
     * Guarda una nueva calificación de película.
     *
     * @param requestRatingFilmDto Datos de la calificación a guardar.
     * @return Un objeto {@link RatingResponseFilmDto} con los detalles de la calificación guardada.
     * @throws IncorrectResultSizeDataAccessException Si ocurre un error al acceder a los datos.
     * @throws OperationNotAllowException Si ya existe una calificación para el usuario y la película.
     */
    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public RatingResponseFilmDto save(@NonNull RequestRatingFilmDto requestRatingFilmDto) throws IncorrectResultSizeDataAccessException {
        if (ratingFilmDao.findRatingFilmByUserIdAndFilmId(requestRatingFilmDto.userId(), requestRatingFilmDto.filmId()).isPresent()) {
            throw new OperationNotAllowException("Operation not allow!!");
        }

        RatingFilm ratingFilm = createOrEditPopulation(new RatingFilm(), requestRatingFilmDto);

        return new RatingResponseFilmDto(ratingFilm.getScore(), ratingFilm.getCreateAt());
    }

    /**
     * Actualiza una calificación existente.
     *
     * @param filmId ID de la película.
     * @param userId ID del usuario.
     * @param requestRatingFilmDto Datos de la calificación a actualizar.
     * @return Un objeto {@link RatingFilmDto} con los detalles de la calificación actualizada.
     * @throws IncorrectResultSizeDataAccessException Si ocurre un error al acceder a los datos.
     * @throws NotFoundException Si no se encuentra la calificación a actualizar.
     * @throws OperationNotAllowException Si la operación no está permitida debido a duplicados o conflictos.
     */
    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public RatingFilmDto update(@NonNull Long filmId, @NonNull String userId, @NonNull RequestRatingFilmDto requestRatingFilmDto) throws IncorrectResultSizeDataAccessException {
        RatingFilm ratingFilm = ratingFilmDao.findRatingFilmByUserIdAndFilmId(userId, filmId)
                .orElseThrow(() -> new NotFoundException("Register with film id %d and user id %d not found".formatted(filmId, userId)));

        if (ratingFilmDao.findRatingFilmByUserIdAndFilmId(requestRatingFilmDto.userId(), requestRatingFilmDto.filmId()).isPresent()
                && ratingFilm.getScore().setScale(2, RoundingMode.UNNECESSARY).equals(
                requestRatingFilmDto.score().setScale(2, RoundingMode.UNNECESSARY))) {
            throw new OperationNotAllowException("Operation not allowed: duplicate full register or duplicate rating!!");
        }

        if (!filmId.equals(requestRatingFilmDto.filmId()) && !userId.equals(requestRatingFilmDto.userId()) &&
                ratingFilmDao.findRatingFilmByUserIdAndFilmId(requestRatingFilmDto.userId(), requestRatingFilmDto.filmId()).isPresent()) {
            throw new OperationNotAllowException("Operation not allowed: duplicate register!");
        }

        if (!filmId.equals(requestRatingFilmDto.filmId()) &&
                ratingFilmDao.findRatingFilmByUserIdAndFilmId(requestRatingFilmDto.userId(), requestRatingFilmDto.filmId()).isPresent()) {
            throw new OperationNotAllowException("Operation not allowed: film with other rating!");
        }

        if (!userId.equals(requestRatingFilmDto.userId()) &&
                ratingFilmDao.findRatingFilmByUserIdAndFilmId(requestRatingFilmDto.userId(), requestRatingFilmDto.filmId()).isPresent()) {
            throw new OperationNotAllowException("Operation not allowed: user with other rating!");
        }

        ratingFilm = createOrEditPopulation(ratingFilm, requestRatingFilmDto);
        return buildRatingFilmDto(ratingFilm);
    }

    /**
     * Encuentra una calificación de película por el ID de la película y el ID del usuario.
     *
     * @param filmId ID de la película.
     * @param userId ID del usuario.
     * @return Un objeto {@link RatingFilmDto} con los detalles de la calificación encontrada.
     * @throws IncorrectResultSizeDataAccessException Si ocurre un error al acceder a los datos.
     * @throws NotFoundException Si no se encuentra la calificación.
     */
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public RatingFilmDto findRatingByFilmAndUserHandler(@NonNull Long filmId, @NonNull String userId) throws IncorrectResultSizeDataAccessException {
        RatingFilm ratingFilm = ratingFilmDao.findRatingFilmByUserIdAndFilmId(userId, filmId)
                .orElseThrow(() -> new NotFoundException("Rating Film not found!"));
        return buildRatingFilmDto(ratingFilm);
    }

    /**
     * Elimina una calificación de película por el ID de la película y el ID del usuario.
     *
     * @param filmId ID de la película.
     * @param userId ID del usuario.
     * @throws IncorrectResultSizeDataAccessException Si ocurre un error al acceder a los datos.
     * @throws NotFoundException Si no se encuentra la calificación a eliminar.
     */
    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void deleteByFilmIdAndUserId(@NonNull Long filmId, @NonNull String userId) throws IncorrectResultSizeDataAccessException {
        RatingFilm ratingFilm = ratingFilmDao.findRatingFilmByUserIdAndFilmId(userId, filmId).orElseThrow(
                () -> new NotFoundException("Ratings not found!")
        );

        ratingFilmDao.delete(ratingFilm);
    }

    /**
     * Calcula el promedio de calificaciones para una película.
     *
     * @param filmId ID de la película.
     * @return Un objeto {@link AverageRating} con el promedio de calificaciones.
     * @throws NotFoundException Si no hay calificaciones para la película.
     */
    @Override
    public AverageRating averageRatings(Long filmId) throws NotFoundException {
        return ratingFilmDao.mainScoreByFilmId(filmId).orElseThrow(() -> new NotFoundException("There aren't account by film"));
    }

    /**
     * Crea o actualiza una calificación de película.
     *
     * @param ratingFilm Objeto {@link RatingFilm} a actualizar o crear.
     * @param requestRatingFilmDto Datos de la calificación.
     * @return El objeto {@link RatingFilm} actualizado o creado.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    protected RatingFilm createOrEditPopulation(RatingFilm ratingFilm, RequestRatingFilmDto requestRatingFilmDto) {
        ratingFilm.setFilmId(requestRatingFilmDto.filmId());
        ratingFilm.setUserId(requestRatingFilmDto.userId());
        ratingFilm.setScore(requestRatingFilmDto.score());

        if (ratingFilm.getId() != null) {
            ratingFilm.setUpdateAt(LocalDateTime.now());
        }

        return ratingFilmDao.saveAndFlush(ratingFilm);
    }

    /**
     * Construye un objeto {@link RatingFilmDto} a partir de un objeto {@link RatingFilm}.
     *
     * @param ratingFilm Objeto {@link RatingFilm} fuente.
     * @return Un objeto {@link RatingFilmDto} con los datos mapeados.
     */
    private static RatingFilmDto buildRatingFilmDto(@NonNull RatingFilm ratingFilm) {
        return RatingFilmDto.builder()
                .id(ratingFilm.getId())
                .userId(ratingFilm.getUserId())
                .filmId(ratingFilm.getFilmId())
                .score(ratingFilm.getScore())
                .createAt(DateHelper.parseLocalDateToTimeToOffsetDateTimeUtc(ratingFilm.getCreateAt()))
                .updatedAt(DateHelper.parseLocalDateToTimeToOffsetDateTimeUtc(ratingFilm.getUpdateAt()))
                .build();
    }
}