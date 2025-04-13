package com.tokioschool.filmapp.mapper;

import com.tokioschool.configs.ModelMapperConfiguration;
import com.tokioschool.filmapp.domain.Artist;
import com.tokioschool.filmapp.domain.Movie;
import com.tokioschool.filmapp.dto.movie.MovieDto;
import com.tokioschool.filmapp.enums.TYPE_ARTIST;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ModelMapperConfiguration.class,MovieToMovieDtoMapper.class})
@ActiveProfiles("test")
public class MovieToMovieDtoMapperUTest {

    @Autowired
    private ModelMapper modelMapper;

    @Test
    void givenMovie_whenMapperToMovieDto_whenMovieDto(){
        final Artist manager = Artist.builder()
                .name("artist")
                .surname("surname")
                .typeArtist(TYPE_ARTIST.DIRECTOR).build();

        final List<Artist> artists = new ArrayList<>();
        artists.add(Artist.builder()
                        .name("artist1")
                        .surname("surname")
                        .typeArtist(TYPE_ARTIST.ACTOR).build());
        artists.add(Artist.builder()
                        .name("artist2")
                        .surname("surname")
                        .typeArtist(TYPE_ARTIST.ACTOR).build());

        Movie movie = Movie.builder()
                .id(1L)
                .title("String Fight")
                .releaseYear(1992)
                .manager(manager)
                .artists(artists)
                .build();

        final MovieDto movieDto = modelMapper.map(movie, MovieDto.class);

        Assertions.assertThat(movieDto)
                .returns(movie.getTitle(),MovieDto::getTitle)
                .returns(movie.getReleaseYear(),MovieDto::getReleaseYear)
                .returns(movie.getArtists().getFirst().getName(),movieDto1 -> movieDto1.getArtistDtos().getFirst().getName())
                .returns(movie.getManager().getName(),movieDto1 -> movieDto1.getManagerDto().getName());
    }
}

