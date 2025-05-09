package com.tokioschool.filmapp.services.artist.impl;

import com.github.javafaker.Faker;
import com.tokioschool.filmapp.converter.ArtistListToArtistDtoListConverter;
import com.tokioschool.filmapp.converter.ArtistToArtistDtoConverter;
import com.tokioschool.filmapp.converter.UUIDToStringConverter;
import com.tokioschool.filmapp.domain.Artist;
import com.tokioschool.filmapp.domain.Movie;
import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.dto.movie.MovieDto;
import com.tokioschool.filmapp.enums.TYPE_ARTIST;
import com.tokioschool.filmapp.repositories.ArtistDao;
import com.tokioschool.filmapp.repositories.MovieDao;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ArtistServiceImplUTest {

    @Mock
    private ArtistDao artistDao;

    @Mock
    private MovieDao movieDao;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private ArtistServiceImpl artistService;


    private static List<Movie> movies;

    @BeforeAll
    public static void initGlobal(){
        final Faker faker = new Faker();

        movies = IntStream.range(1,16)
                .mapToObj(i ->{
                    final Artist manager = com.tokioschool.filmapp.domain.Artist.builder()
                            .name(faker.name().fullName())
                            .surname(faker.name().username())
                            .typeArtist(TYPE_ARTIST.DIRECTOR).build();

                    final List<Artist> artists = new ArrayList<>();
                    artists.add(com.tokioschool.filmapp.domain.Artist.builder()
                            .name(faker.name().fullName())
                            .surname(faker.name().username())
                            .typeArtist(TYPE_ARTIST.ACTOR).build());
                    artists.add(com.tokioschool.filmapp.domain.Artist.builder()
                            .name(faker.name().fullName())
                            .surname(faker.name().username())
                            .typeArtist(TYPE_ARTIST.ACTOR).build());

                    return Movie.builder()
                            .id((long)i)
                            .title(faker.name().title())
                            .releaseYear(faker.number().numberBetween(1960,2020))
                            .manager(manager)
                            .artists(artists)
                            .build();
                }).toList();
    }

    @BeforeEach
    void init(){
        // configuration of mapper
        this.modelMapper.typeMap(Movie.class, MovieDto.class)
                .addMappings(mapping -> mapping.using(new ArtistToArtistDtoConverter())
                        .map(Movie::getManager,MovieDto::setManagerDto))
                .addMappings(mapping -> mapping.using(new ArtistListToArtistDtoListConverter())
                        .map(Movie::getArtists,MovieDto::setArtistDtos))
                .addMappings(mapping -> mapping.using(new UUIDToStringConverter())
                        .map(Movie::getImage,MovieDto::setResourceId));
    }

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

    @Test
    void givenIdManager_genFindMoviesByManger_thenReturnList(){
        Mockito.when( movieDao.findMovieByManagerId( 1L ) ).thenReturn( List.of( movies.getFirst() ) );

        List<MovieDto> moviesFind = artistService.findMoviesByManagerById( 1L );

        Assertions.assertThat(moviesFind).isNotEmpty()
                .first()
                .returns(movies.getFirst().getId(), MovieDto::getId)
                .returns(movies.getFirst().getTitle(), MovieDto::getTitle)
                .returns(movies.getFirst().getReleaseYear(), MovieDto::getReleaseYear)
                .satisfies( movieDto -> movieDto.getManagerDto().getName().equalsIgnoreCase( movies.getFirst().getManager().getName()) );

    }

    @Test
    void givenIdManager_genFindMoviesByManger_thenReturnListEmpty(){
        Mockito.when( movieDao.findMovieByManagerId( 1L ) ).thenReturn( List.of( movies.getFirst() ) );

        List<MovieDto> moviesFind = artistService.findMoviesByManagerById( 1L );

        Assertions.assertThat(moviesFind).isNotEmpty()
                .first()
                .returns(movies.getFirst().getId(), MovieDto::getId)
                .returns(movies.getFirst().getTitle(), MovieDto::getTitle)
                .returns(movies.getFirst().getReleaseYear(), MovieDto::getReleaseYear)
                .satisfies( movieDto -> movieDto.getManagerDto().getName().equalsIgnoreCase( movies.getFirst().getManager().getName()) )
                .extracting(MovieDto::getArtistDtos)
                .isNotNull();

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