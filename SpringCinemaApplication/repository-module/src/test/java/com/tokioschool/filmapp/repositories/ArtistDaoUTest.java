package com.tokioschool.filmapp.repositories;

import com.github.javafaker.Faker;
import com.tokioschool.filmapp.domain.Artist;
import com.tokioschool.filmapp.enums.TYPE_ARTIST;
import com.tokioschool.filmapp.repositories.configuration.TestConfig;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.stream.IntStream;

@DataJpaTest
@ContextConfiguration(classes = {TestConfig.class,ArtistDao.class})
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ArtistDaoUTest {

    @Autowired private ArtistDao artistDao;
    @Autowired private Faker faker;

    private static List<Artist> artists;












































    @BeforeEach
    void init(){
        TYPE_ARTIST[] types = TYPE_ARTIST.values();
        artists = IntStream.range(0,10).mapToObj(i -> {
            return Artist.builder()
                            .name(faker.superhero().name())
                            .surname(faker.superhero().prefix())
                            .typeArtist(types[getRandom(0, types.length-1)])
                            .build();
        }).toList();

        artistDao.saveAll(artists);
    }

    @Test
    void givenTypeActor_whenGetArtistsByTypeArtist_thenReturnOk() {

        List<Artist> actors = artistDao.getArtistsByTypeArtist(TYPE_ARTIST.ACTOR);

        Assertions.assertThat(actors)
                .isNotEmpty();
    }

    @Test
    void givenArtist_whenSave_thenReturnError() {

        Artist presentador = Artist.builder()
                .name(faker.superhero().name())
                .surname(faker.superhero().prefix())
                .typeArtist(TYPE_ARTIST.DIRECTOR)
                .build();

        presentador = artistDao.save(presentador);
        Assertions.assertThat(presentador)
                .satisfies(artist -> Assertions.assertThat(artist.getId()).isNotNull());
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