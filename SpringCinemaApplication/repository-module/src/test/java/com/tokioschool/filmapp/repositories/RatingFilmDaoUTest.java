package com.tokioschool.filmapp.repositories;

import com.tokioschool.filmapp.domain.RatingFilm;
import com.tokioschool.filmapp.records.AverageRating;
import com.tokioschool.filmapp.repositories.configuration.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
@ContextConfiguration(classes = {TestConfig.class,RatingFilmDao.class})
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RatingFilmDaoUTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RatingFilmDao ratingFilmDao;

    @BeforeEach
    public void init(){
        RatingFilm rating1 = new RatingFilm();
        rating1.setUserId("1L");
        rating1.setFilmId(1L);
        rating1.setScore(BigDecimal.valueOf(4.0));
        entityManager.persistAndFlush(rating1);

        RatingFilm rating2 = new RatingFilm();
        rating2.setUserId("2L");
        rating2.setFilmId(1L);
        rating2.setScore(BigDecimal.valueOf(5.0));
        entityManager.persistAndFlush(rating2);
    }

    @Test
    void findRatingFilmByUserIdAndFilmId_withInvalidUserIdAndFilmId_shouldReturnRegister() {
        Optional<RatingFilm> result = ratingFilmDao.findRatingFilmByUserIdAndFilmId("1L", 1L);

        assertThat(result).isNotNull();
        assertThat(result)
                .isPresent()
                .get()
                .returns(1L,RatingFilm::getFilmId)
                .returns("1L",RatingFilm::getUserId)
                .satisfies(ratingFilm -> BigDecimal.valueOf(4.0).equals( ratingFilm.getScore() ));
    }


    @Test
    void findRatingFilmByUserIdAndFilmId_withValidUserIdAndFilmIdRepeat_shouldReturnIncorrectResultSizeDataAccessException() {
        RatingFilm rating1 = new RatingFilm();
        rating1.setUserId("1L");
        rating1.setFilmId(1L);
        rating1.setScore(BigDecimal.valueOf(4.0));
        ratingFilmDao.saveAndFlush(rating1);

        assertThatThrownBy( () -> ratingFilmDao.findRatingFilmByUserIdAndFilmId("1L", 1L) )
                .isInstanceOf(IncorrectResultSizeDataAccessException.class);

    }

    @Test
    void findRatingFilmByUserIdAndFilmId_withInvalidUserIdAndFilmId_shouldReturnEmptyList() {
        Optional<RatingFilm> result = ratingFilmDao.findRatingFilmByUserIdAndFilmId("3L", 999L);

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void mainScoreByFilmId_withValidFilmId_shouldReturnAverageScore() {

        Optional<AverageRating> result = ratingFilmDao.mainScoreByFilmId(1L);

        assertThat(result)
                .isNotNull()
                .isPresent()
                .get()
                .returns(4.5,AverageRating::average)
                .returns(2L,AverageRating::ratings);
    }

    @Test
    void mainScoreByFilmId_withInvalidFilmId_shouldReturnNull() {
        Optional<AverageRating>  result = ratingFilmDao.mainScoreByFilmId(99L);

        assertThat(result).isNotNull().isEmpty();
    }
}