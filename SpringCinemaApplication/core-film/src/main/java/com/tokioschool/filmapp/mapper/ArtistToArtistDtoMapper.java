package com.tokioschool.filmapp.mapper;

import com.tokioschool.filmapp.converter.TypeArtistToStringConverter;
import com.tokioschool.filmapp.domain.Artist;
import com.tokioschool.filmapp.dto.artist.ArtistDto;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for mapper the fields of Artist to ArtistDtO
 *
 * @version 1.0
 * @author andres.rpenuela
 */
@Configuration
public class ArtistToArtistDtoMapper {

    // override to bean of ModelMapper of ModelMapperConfiguration
    private final ModelMapper modelMapper;

    public ArtistToArtistDtoMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
        configureUserDTOConverter();
    }

    private void configureUserDTOConverter(){
        modelMapper.typeMap(Artist.class, ArtistDto.class)
                .addMappings(mapping ->
                        mapping.using(new TypeArtistToStringConverter() )
                                .map(Artist::getTypeArtist,ArtistDto::setTypeArtist));
    }
}
