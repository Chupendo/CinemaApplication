package com.tokioschool.filmapp.repositories;

import com.github.javafaker.Faker;
import com.tokioschool.filmapp.domain.Artist;
import com.tokioschool.filmapp.domain.Movie;
import com.tokioschool.filmapp.enums.TYPE_ARTIST;
import com.tokioschool.filmapp.projections.ResultMovie;
import com.tokioschool.filmapp.repositories.configuration.TestConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@DataJpaTest
@ContextConfiguration(classes = {TestConfig.class,ArtistDao.class,MovieDao.class})
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MovieDaoUTest {

    @Autowired private MovieDao movieDao;
    @Autowired private ArtistDao artistDao;
    @Autowired private Faker faker;

    List<Movie> movies;
    List<Artist> artists;
    @BeforeEach
    void init(){
        // movies
        movies = IntStream.range(1980,1990).mapToObj(
                i ->{
                    // artist
                    TYPE_ARTIST[] types = TYPE_ARTIST.values();
                    artists = IntStream.range(1,3).mapToObj(j ->
                         Artist.builder()
                                .name(faker.superhero().name())
                                .surname(faker.superhero().prefix())
                            .typeArtist(types[getRandom(0, types.length-1)])
                                .build()
                    ).toList();

                    artistDao.saveAll(artists);

                    return Movie.builder()
                            .title(faker.pokemon().name())
                            .releaseYear(i)
                            .manager(artists.getFirst())
                            .artists(artists)
                            .build();
                }
        ).toList();

        movieDao.saveAll(movies);
    }

    @Test
    @Order(1)
    void givenTitle_whenFindMovieByTitleContainsIgnoreCase_thenReturnAListNoEmpty() {

        List<ResultMovie> resultMovies = movieDao.findMovieByTitleContainsIgnoreCase(movies.getFirst().getTitle());

        Assertions.assertThat(resultMovies)
                .isNotEmpty()
                .hasSizeGreaterThanOrEqualTo(1)
                .first()
                .satisfies(resultMovie ->
                        org.junit.jupiter.api.Assertions.assertTrue(
                                resultMovies.getFirst().getTitle().contains(movies.getFirst().getTitle())));
    }

    @Test
    @Order(2)
    void givenBetweenYearWithError_whenFindMovieByReleaseYearIsBetween_thenReturnAListEmpty() {

        List<ResultMovie> resultMovies = movieDao.findMovieByReleaseYearBetween(1980,null);

        Assertions.assertThat(resultMovies)
                .isEmpty();
    }

    @Test
    @Order(3)
    void givenBetweenYear_whenFindMovieByReleaseYearIsBetween_thenReturnAListNoEmpty() {

        List<ResultMovie> resultMovies = movieDao.findMovieByReleaseYearBetween(1980,1982);

        Assertions.assertThat(resultMovies)
                .isNotEmpty()
                .hasSize(3);// collection of three movies, with years: 1980, 1981 and 1982
    }

    @Test
    @Order(4)
    void givenYear_whenFindMovieByReleaseYearIn_thenReturnAListNoEmpty() {

        List<ResultMovie> resultMovies = movieDao.findMovieByReleaseYearIs(1980);

        Assertions.assertThat(resultMovies)
                .isNotEmpty()
                .hasSize(1 );
    }

    /**
     * Get a number random between [min,max]
     *
     * @param min
     * @param max
     * @return number random
     */
    private static int getRandom(int min, int max) {
        int range = (max - min) + 1;
        int random = (int) ((range * Math.random()) + min);
        return random;
    }
}