package com.tokioschool.filmapp.mapper;

import com.tokioschool.filmapp.converter.ArtistListToArtistDtoListConverter;
import com.tokioschool.filmapp.converter.ArtistToArtistDtoConverter;
import com.tokioschool.filmapp.converter.UUIDToStringConverter;
import com.tokioschool.filmapp.domain.Movie;
import com.tokioschool.filmapp.dto.movie.MovieDto;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MovieToMovieDtoMapper {

    // override to bean of ModelMapper of ModelMapperConfiguration
    private final ModelMapper modelMapper;

    MovieToMovieDtoMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
        init();
    }

    private void init(){
        this.modelMapper.typeMap(Movie.class, MovieDto.class)
                .addMappings(mapping -> mapping.using(new ArtistToArtistDtoConverter())
                        .map(Movie::getManager,MovieDto::setManagerDtoId))
                .addMappings(mapping -> mapping.using(new ArtistListToArtistDtoListConverter())
                        .map(Movie::getArtists,MovieDto::setArtistDtos))
                .addMappings(mapping -> mapping.using(new UUIDToStringConverter())
                        .map(Movie::getImage,MovieDto::setResourceId));
    }

}
