package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.domain.Artist;
import com.tokioschool.filmapp.dto.artist.ArtistDto;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;

import java.util.List;
import java.util.Optional;

public class ArtistToArtistDtoConverter implements Converter<Artist,ArtistDto> {

    @Override
    public ArtistDto convert(MappingContext<Artist, ArtistDto> context) {
        final ModelMapper modelMapper = new ModelMapper();

        return Optional.ofNullable(context.getSource())
                .map(artist -> modelMapper.map(artist,ArtistDto.class))
                .orElseGet(() -> null);
    }
}
