package com.tokioschool.ratingapp.reports;

import com.tokioschool.ratingapp.domains.RatingFilm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RatingFilmDao extends JpaRepository<RatingFilm,Long> {

    List<RatingFilm> findRatingFilmByUserIdAndFilmId(String userId, Long filmId);

    @Query("SELECT AVG(r.score) FROM RatingFilm r WHERE r.filmId = ?1")
    BigDecimal mainScoreByFilmId(Long filmId);
}
