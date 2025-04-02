package com.tokioschool.ratingapp.repositories;

import com.tokioschool.ratingapp.domains.RatingFilm;
import com.tokioschool.ratingapp.dtos.AverageRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingFilmDao extends JpaRepository<RatingFilm,Long> {

    Optional<RatingFilm> findRatingFilmByUserIdAndFilmId(Long userId, Long filmId);

    @Query("SELECT new com.tokioschool.ratingapp.dtos.AverageRating( AVG(r.score), count(*) )FROM RatingFilm r WHERE r.filmId = ?1 group by r.filmId")
    Optional<AverageRating> mainScoreByFilmId(Long filmId);
 }
