package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.domain.Artist;
import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.enums.TYPE_ARTIST;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.spi.MappingContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
public class ArtistToArtistDtoConverterUTest {

    ArtistToArtistDtoConverter converter;

    @BeforeEach
    void setUp() {
        converter = new ArtistToArtistDtoConverter();
    }

    @Test
    void convert_withValidArtist_shouldReturnArtistDto() {
        Artist artist =    Artist.builder().id(1L).typeArtist(TYPE_ARTIST.ACTOR).name("Artist 1").build();

        MappingContext<Artist, ArtistDto> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(artist);

        ArtistDto result = converter.convert(context);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Artist 1");
        assertThat(result.getTypeArtist()).isEqualTo(TYPE_ARTIST.ACTOR.name());
    }


    @Test
    void convert_withNullArtist_shouldReturnNull() {
        MappingContext<Artist, ArtistDto> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(null);

        ArtistDto result = converter.convert(context);

        assertThat(result).isNull();
    }

    @Test
    void convert_withNullNull_shouldReturnNull() {

        ArtistDto result = converter.convert(null);

        assertThat(result).isNull();
    }

}
