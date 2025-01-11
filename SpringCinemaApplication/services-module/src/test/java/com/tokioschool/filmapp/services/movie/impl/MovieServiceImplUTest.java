package com.tokioschool.filmapp.services.movie.impl;

import com.github.javafaker.Faker;
import com.tokioschool.filmapp.domain.Artist;
import com.tokioschool.filmapp.domain.Movie;
import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.movie.MovieDto;
import com.tokioschool.filmapp.enums.TYPE_ARTIST;
import com.tokioschool.filmapp.records.SearchMovieRecord;
import com.tokioschool.filmapp.repositories.MovieDao;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.class)
class MovieServiceImplUTest {

    @Mock
    private MovieDao movieDao;
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
        Mockito.when(movieDao.findAll()).thenReturn(movies);
    }


    @Test
    @Order(1)
    void givenPageGreaterThatMoviesSize_whenSearchMovie_thenReturnPageWithListEmpty() {
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
    @Order(6)
    void givenSearchWitchPageSizeZeroAndFilter_whenSearchMovie_thenReturnAllItemsInPage() {
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

}