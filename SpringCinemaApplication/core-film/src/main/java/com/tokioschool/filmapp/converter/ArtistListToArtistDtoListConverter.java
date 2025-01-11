package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.domain.Artist;
import com.tokioschool.filmapp.dto.artist.ArtistDto;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;

import java.util.List;

public class ArtistListToArtistDtoListConverter implements Converter<List<Artist>,List<ArtistDto>> {

    @Override
    public List<ArtistDto> convert(MappingContext<List<Artist>, List<ArtistDto>> context) {
        final ModelMapper modelMapper = new ModelMapper();

        return context.getSource().stream().map(artist -> modelMapper.map(artist,ArtistDto.class)).toList();
    }
}
