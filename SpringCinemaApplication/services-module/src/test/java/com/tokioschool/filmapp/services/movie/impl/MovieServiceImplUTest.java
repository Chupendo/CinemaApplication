package com.tokioschool.filmapp.services.movie.impl;

import com.github.javafaker.Faker;
import com.tokioschool.core.exception.NotFoundException;
import com.tokioschool.core.exception.ValidacionException;
import com.tokioschool.filmapp.domain.Artist;
import com.tokioschool.filmapp.domain.Movie;
import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.movie.MovieDto;
import com.tokioschool.filmapp.enums.TYPE_ARTIST;
import com.tokioschool.filmapp.records.SearchMovieRecord;
import com.tokioschool.filmapp.repositories.MovieDao;
import com.tokioschool.filmapp.services.artist.impl.ArtistServiceImpl;
import com.tokioschool.filmapp.services.movie.MovieService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MovieServiceImplUTest {

    @Mock
    private MovieDao movieDao;
    @Mock
    private ArtistServiceImpl artistService;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private MovieServiceImpl movieService;

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
    public void initEach(){

    }


    @Test
    @Order(1)
    void givenPageGreaterThatMoviesSize_whenSearchMovie_thenReturnPageWithListEmpty() {
        Mockito.when(movieDao.findAll()).thenReturn(movies);

        SearchMovieRecord searchMovieRecord = SearchMovieRecord.builder()
                .page(movies.size())
                .pageSize(MovieService.PAGE_SIZE_DEFAULT)
                .build();

        PageDTO<MovieDto> resultMoviePageDTO = movieService.searchMovie(searchMovieRecord);

        Assertions.assertThat(resultMoviePageDTO)
                .isNotNull()
                .returns(true,resultMoviePageDTO1 -> resultMoviePageDTO1.getItems().isEmpty());

        Mockito.verify(modelMapper,Mockito.times(0))
                .map(Mockito.any(Movie.class),Mockito.eq(MovieDto.class));

    }

    @Test
    @Order(2)
    void givenSearchPageOneWithTwoItems_whenSearchMovie_thenReturnPage() {
        Mockito.when(movieDao.findAll()).thenReturn(movies);

        SearchMovieRecord searchMovieRecord = SearchMovieRecord.builder()
                .page(1)
                .pageSize(2)
                .build();

        PageDTO<MovieDto> resultMoviePageDTO = movieService.searchMovie(searchMovieRecord);

        Assertions.assertThat(resultMoviePageDTO)
                .isNotNull()
                .returns(false,movieDtoPageDTO -> movieDtoPageDTO.getItems().isEmpty())
                .returns(searchMovieRecord.pageSize(),movieDtoPageDTO -> movieDtoPageDTO.getItems().size() )
                .returns(searchMovieRecord.pageSize(),PageDTO::getPageSize)
                .returns(searchMovieRecord.page(),PageDTO::getPageNumber)
                .returns((int) Math.ceil(movies.size()/(double)searchMovieRecord.pageSize()),PageDTO::getTotalPages);

        Mockito.verify(modelMapper,Mockito.times(searchMovieRecord.pageSize()))
                .map(Mockito.any(Movie.class),Mockito.eq(MovieDto.class));
    }

    @Test
    @Order(3)
    void givenSearchPageGreaterThatItems_whenSearchMovie_thenReturnPageWitOutItems() {
        Mockito.when(movieDao.findAll()).thenReturn(movies);

        SearchMovieRecord searchMovieRecord = SearchMovieRecord.builder()
                .page(8) // overwritten
                .pageSize(2)
                .build();

        PageDTO<MovieDto> resultMoviePageDTO = movieService.searchMovie(searchMovieRecord);

        Assertions.assertThat(resultMoviePageDTO)
                .isNotNull()
                .returns(true,movieDtoPageDTO -> movieDtoPageDTO.getItems().isEmpty())
                .returns(searchMovieRecord.pageSize(),PageDTO::getPageSize)
                .returns(searchMovieRecord.page(),PageDTO::getPageNumber)
                .returns((int) Math.ceil(movies.size()/(double)searchMovieRecord.pageSize()),PageDTO::getTotalPages);

        Mockito.verify(modelMapper,Mockito.times(0))
                .map(Mockito.any(Movie.class),Mockito.eq(MovieDto.class));
    }

    @Test
    @Order(4)
    void givenSearchByTitle_whenSearchMovie_thenReturnPage() {
        Mockito.when(movieDao.findAll()).thenReturn(movies);

        SearchMovieRecord searchMovieRecord = SearchMovieRecord.builder()
                .title(movies.getFirst().getTitle())
                .page(0) // overwritten
                .pageSize(2)
                .build();

        PageDTO<MovieDto> resultMoviePageDTO = movieService.searchMovie(searchMovieRecord);

        Assertions.assertThat(resultMoviePageDTO)
                .isNotNull()
                .returns(false,movieDtoPageDTO -> movieDtoPageDTO.getItems().isEmpty())
                .returns((int) movies.stream().filter(movie -> movie.getTitle().contains(movies.getFirst().getTitle())).count(),
                        movieDtoPageDTO -> movieDtoPageDTO.getItems().size() )
                .returns(searchMovieRecord.pageSize(),PageDTO::getPageSize)
                .returns(searchMovieRecord.page(),PageDTO::getPageNumber)
                .returns((int) Math.ceil(1/(double)searchMovieRecord.pageSize()),PageDTO::getTotalPages);

        Mockito.verify(modelMapper,Mockito.times(1))
                .map(Mockito.any(Movie.class),Mockito.eq(MovieDto.class));
    }

    @Test
    @Order(5)
    void whenSearchMovieWithOutFilter_thenReturnPageDefault() {
        Mockito.when(movieDao.findAll()).thenReturn(movies);

        PageDTO<MovieDto> resultMoviePageDTO = movieService.searchMovie();

        Assertions.assertThat(resultMoviePageDTO)
                .isNotNull()
                .returns(MovieService.PAGE_DEFAULT,PageDTO::getPageNumber)
                .returns(MovieService.PAGE_SIZE_DEFAULT,PageDTO::getPageSize)
                .returns((int) Math.ceil(movies.size()/(double)MovieService.PAGE_SIZE_DEFAULT),PageDTO::getTotalPages);


        Mockito.verify(modelMapper,Mockito.times(resultMoviePageDTO.getPageSize()))
                .map(Mockito.any(Movie.class),Mockito.eq(MovieDto.class));
    }

    @Test
    @Order(6)
    void givenSearchWitchPageSizeZero_whenSearchMovie_thenReturnAllItemsInPage() {
        Mockito.when(movieDao.findAll()).thenReturn(movies);

        SearchMovieRecord searchMovieRecord = SearchMovieRecord.builder()
                .page(0) // overwritten
                .pageSize(0)
                .build();

        PageDTO<MovieDto> resultMoviePageDTO = movieService.searchMovie(searchMovieRecord);

        Assertions.assertThat(resultMoviePageDTO)
                .isNotNull()
                .returns(false,movieDtoPageDTO -> movieDtoPageDTO.getItems().isEmpty() )
                .returns(movies.size(),movieDtoPageDTO -> movieDtoPageDTO.getItems().size() )
                .returns(searchMovieRecord.page(),PageDTO::getPageNumber)
                .returns(movies.size(),PageDTO::getPageSize)
                .returns(1,PageDTO::getTotalPages);

        Mockito.verify(modelMapper,Mockito.times(movies.size()))
                .map(Mockito.any(Movie.class),Mockito.eq(MovieDto.class));
    }

    @Test
    @Order(7)
    void givenSearchWitchPageSizeZeroAndFilter_whenSearchMovie_thenReturnAllItemsInPage() {
        Mockito.when(movieDao.findAll()).thenReturn(movies);

        SearchMovieRecord searchMovieRecord = SearchMovieRecord.builder()
                .title("l")
                .page(0) // overwritten
                .pageSize(0)
                .build();

        final int maybeMatch = (int) movies.stream()
                .filter(movie ->
                        movie.getTitle()
                                .toUpperCase()
                                .contains(searchMovieRecord.title().toUpperCase()))
                .count();

        PageDTO<MovieDto> resultMoviePageDTO = movieService.searchMovie(searchMovieRecord);

        Assertions.assertThat(resultMoviePageDTO)
                .isNotNull()
                .returns(false,movieDtoPageDTO -> movieDtoPageDTO.getItems().isEmpty() )
                .returns(maybeMatch,
                        movieDtoPageDTO -> movieDtoPageDTO.getItems().size() )
                .returns(searchMovieRecord.page(),PageDTO::getPageNumber)
                .returns(maybeMatch,PageDTO::getPageSize)
                .returns(1,PageDTO::getTotalPages);

        Mockito.verify(modelMapper,Mockito.times(maybeMatch))
                .map(Mockito.any(Movie.class),Mockito.eq(MovieDto.class));
    }


    @Test
    @Order(8)
    void givenIdNull_whenSearchMovie_thenNotFoundException() {
        Mockito.when(movieDao.findById(null)).thenThrow(new InvalidDataAccessApiUsageException("Movie don't found in the system"));

        Assertions.assertThatThrownBy(()-> movieService.getMovieById(null))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    @Order(9)
    void givenValidMovieDto_whenCreateMovie_thenReturnMovieDto() {
        MovieDto movieDto = new MovieDto();
        movieDto.setTitle("Test Movie");
        movieDto.setManagerDto(new ArtistDto(1L, "Manager", "Surname", "DIRECTOR"));
        movieDto.setArtistDtos(List.of(new ArtistDto(2L, "Actor", "Surname", "ACTOR")));
        movieDto.setReleaseYear(2020);
        movieDto.setResourceId("123e4567-e89b-12d3-a456-426614174000");

        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Test Movie");
        movie.setManager(new Artist(1L, "Manager", "Surname", TYPE_ARTIST.DIRECTOR));
        movie.setArtists(List.of(new Artist(2L, "Actor", "Surname", TYPE_ARTIST.ACTOR)));
        movie.setReleaseYear(2020);
        movie.setImage(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));

        Mockito.when(movieDao.saveAndFlush(Mockito.any(Movie.class))).thenReturn(movie);
        //Mockito.when(modelMapper.map(Mockito.any(Movie.class), Mockito.eq(MovieDto.class))).thenReturn(movieDto);
        Mockito.when(artistService.findById(1L)).thenReturn(new ArtistDto(1L, "Manager", "Surname", "DIRECTOR"));
        Mockito.when(artistService.findById(2L)).thenReturn(new ArtistDto(2L, "Actor", "Surname", "ACTOR"));

        MovieDto result = movieService.createMovie(movieDto);

        Assertions.assertThat(result).isNotNull()
                .returns(movieDto.getTitle(), MovieDto::getTitle)
                .returns(movieDto.getReleaseYear(), MovieDto::getReleaseYear);

        Mockito.verify(modelMapper,Mockito.times(1))
                .map(Mockito.any(Movie.class),Mockito.eq(MovieDto.class));
    }

    @Test
    @Order(10)
    void givenNullMovieDto_whenCreateMovie_thenThrowValidationException() {
        Assertions.assertThatThrownBy(() -> movieService.createMovie(null))
                .isInstanceOf(ValidacionException.class)
                .hasMessage("movie don't create");
    }

    @Test
    @Order(11)
    void givenMovieDtoWithInvalidArtistId_whenCreateMovie_thenThrowNotFoundException() {
        MovieDto movieDto = new MovieDto();
        movieDto.setTitle("Test Movie");
        movieDto.setManagerDto(new ArtistDto(1L, "Manager", "Surname", "DIRECTOR"));
        movieDto.setArtistDtos(List.of(new ArtistDto(999L, "Actor", "Surname", "ACTOR")));
        movieDto.setReleaseYear(2020);
        movieDto.setResourceId("123e4567-e89b-12d3-a456-426614174000");

        Mockito.when(artistService.findById(Mockito.anyLong())).thenThrow(new NotFoundException("Artist not found"));

        Assertions.assertThatThrownBy(() -> movieService.createMovie(movieDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Artist not found");
    }
}