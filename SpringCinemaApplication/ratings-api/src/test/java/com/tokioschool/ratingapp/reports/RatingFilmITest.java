package com.tokioschool.ratingapp.reports;

import com.tokioschool.ratingapp.domains.RatingFilm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RatingFilmITest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RatingFilmDao ratingFilmDao;

    @Test
    void findRatingFilmByUserIdAndFilmId_withValidUserIdAndFilmId_shouldReturnRatings() {
        RatingFilm rating1 = new RatingFilm();
        rating1.setUserId("user123");
        rating1.setFilmId(1L);
        rating1.setScore(BigDecimal.valueOf(4.0));
        entityManager.persistAndFlush(rating1);

        RatingFilm rating2 = new RatingFilm();
        rating2.setUserId("user123");
        rating2.setFilmId(1L);
        rating2.setScore(BigDecimal.valueOf(5.0));
        entityManager.persistAndFlush(rating2);

        List<RatingFilm> result = ratingFilmDao.findRatingFilmByUserIdAndFilmId("user123", 1L);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void findRatingFilmByUserIdAndFilmId_withInvalidUserIdAndFilmId_shouldReturnEmptyList() {
        List<RatingFilm> result = ratingFilmDao.findRatingFilmByUserIdAndFilmId("invalidUser", 999L);

        assertThat(result).isNotNull();
        assertThat(result).satisfies(List::isEmpty);
    }

    @Test
    void mainScoreByFilmId_withValidFilmId_shouldReturnAverageScore() {
        RatingFilm rating1 = new RatingFilm();
        rating1.setUserId("user123");
        rating1.setFilmId(1L);
        rating1.setScore(BigDecimal.valueOf(4.0));
        entityManager.persistAndFlush(rating1);

        RatingFilm rating2 = new RatingFilm();
        rating2.setUserId("user123");
        rating2.setFilmId(1L);
        rating2.setScore(BigDecimal.valueOf(5.0));
        entityManager.persistAndFlush(rating2);

        BigDecimal result = ratingFilmDao.mainScoreByFilmId(1L);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(BigDecimal.valueOf(4.5));
    }

    @Test
    void mainScoreByFilmId_withInvalidFilmId_shouldReturnNull() {
        BigDecimal result = ratingFilmDao.mainScoreByFilmId(999L);

        assertThat(result).isNull();
    }
}