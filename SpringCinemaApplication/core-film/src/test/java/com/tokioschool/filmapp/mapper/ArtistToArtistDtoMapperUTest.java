package com.tokioschool.filmapp.mapper;

import com.tokioschool.filmapp.domain.Artist;
import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.enums.TYPE_ARTIST;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ModelMapperConfiguration.class,UserToUserDtoMapper.class})
@ActiveProfiles("test")
public class ArtistToArtistDtoMapperUTest {

    @Autowired
    private ModelMapper modelMapper;

    @Test
    void givenUser_whenMapperToUserDto_whenUserDto(){
        final Artist artist = Artist.builder()
                .name("artist")
                .surname("surname")
                .typeArtist(TYPE_ARTIST.ACTOR).build();


        ArtistDto artistDto = modelMapper.map(artist, ArtistDto.class);

        Assertions.assertThat(artistDto)
                .returns(artist.getName(),ArtistDto::getName)
                .returns(artist.getSurname(),ArtistDto::getSurname)
                .returns(artist.getTypeArtist().name().toUpperCase(),ArtistDto::getTypeArtist);
    }
}
