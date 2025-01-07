package com.tokioschool.filmapp.services.artist.impl;

import com.github.javafaker.Faker;
import com.tokioschool.filmapp.domain.Artist;
import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.enums.TYPE_ARTIST;
import com.tokioschool.filmapp.repositories.ArtistDao;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.IntStream;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ArtistServiceImplUTest {

    @Mock
    private ArtistDao artistDao;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private ArtistServiceImpl artistService;


    @Test
    void whenFindAllArtist_returnListArtistDTO(){
        final Faker faker = new Faker();
        List<Artist> artists = IntStream.range(0,3)
                .mapToObj(i -> Artist.builder()
                        .name(faker.name().name())
                        .surname(faker.name().lastName())
                        .typeArtist(TYPE_ARTIST.values()[getRandom(0,TYPE_ARTIST.values().length-1)])
                        .build())
                .toList();

        Mockito.when(artistDao.findAll()).thenReturn(artists);

        List<ArtistDto> artistDtos = artistService.findByAll();

        Assertions.assertThat(artistDtos).isNotEmpty().hasSize(artists.size());
    }

    @Test
    void givenUserDto_whenRegisterArtistReturnOk() {

        ArtistDto artistDto = ArtistDto.builder()
                .name("john")
                .surname("Smith")
                .typeArtist("actor")
                .build();

        Artist artist = Artist.builder()
                .name("john")
                .surname("Smith")
                .typeArtist(TYPE_ARTIST.ACTOR).build();

        Mockito.when(artistDao.save(Mockito.any(Artist.class))).thenReturn(artist);

        ArtistDto resultArtistDto = artistService.registerArtist(artistDto);

        Assertions.assertThat(resultArtistDto).isNotNull()
                .returns("john",ArtistDto::getName)
                .returns("ACTOR",ArtistDto::getTypeArtist);
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