package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.domain.Artist;
import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.enums.TYPE_ARTIST;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.spi.MappingContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
public class ArtistListToArtistDtoListConverterUTest {

    private ArtistListToArtistDtoListConverter converter;

    @BeforeEach
    void setUp() {
        converter = new ArtistListToArtistDtoListConverter();
    }

    @Test
    void convert_withValidArtistList_shouldReturnArtistDtoList() {
        List<Artist> artistList = Stream.of(
                Artist.builder().id(1L).typeArtist(TYPE_ARTIST.ACTOR).name("Artist 1").build(),
                Artist.builder().id(1L).typeArtist(TYPE_ARTIST.DIRECTOR).name("Artist 2").build()

        ).collect(Collectors.toList());

        MappingContext<List<Artist>, List<ArtistDto>> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(artistList);

        List<ArtistDto> result = converter.convert(context);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Artist 1");
        assertThat(result.get(0).getTypeArtist()).isEqualTo(TYPE_ARTIST.ACTOR.name());
        assertThat(result.get(1).getName()).isEqualTo("Artist 2");
        assertThat(result.get(1).getTypeArtist()).isEqualTo(TYPE_ARTIST.DIRECTOR.name());
    }

    @Test
    void convert_withEmptyArtistList_shouldReturnEmptyArtistDtoList() {
        List<Artist> artistList = List.of();

        MappingContext<List<Artist>, List<ArtistDto>> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(artistList);

        List<ArtistDto> result = converter.convert(context);

        assertThat(result).isEmpty();
    }

    @Test
    void convert_withNullArtistList_shouldReturnListEmpty() {
        MappingContext<List<Artist>, List<ArtistDto>> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(null);

        List<ArtistDto> result = converter.convert(context);

        assertThat(result).isEmpty();
    }

    @Test
    void convert_withNull_shouldReturnListEmpty() {
                List<ArtistDto> result = converter.convert(null);

        assertThat(result).isEmpty();
    }
}
