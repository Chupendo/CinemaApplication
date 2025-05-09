package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.enums.TYPE_ARTIST;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.spi.MappingContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class TypeArtistToStringConverterUTest {

    private final TypeArtistToStringConverter converter = new TypeArtistToStringConverter();

    @Test
    void convert_withValidTypeArtist_shouldReturnNameUpperCase() {

        // Arrange
        MappingContext<TYPE_ARTIST, String> context = Mockito.mock(MappingContext.class);
        Mockito.when(context.getSource()).thenReturn(TYPE_ARTIST.DIRECTOR);

        // Act
        String typeArtist = converter.convert(context);

        // Assertions
        Assertions.assertThat(typeArtist)
                .isEqualTo(TYPE_ARTIST.DIRECTOR.name().toUpperCase());
    }

    @Test
    void convert_withValidTypeArtistNull_shouldReturnNull() {

        // Arrange
        MappingContext<TYPE_ARTIST, String> context = Mockito.mock(MappingContext.class);
        Mockito.when(context.getSource()).thenReturn(null);

        // Act
        String typeArtist = converter.convert(context);

        // Assertions
        Assertions.assertThat(typeArtist).isNull();
    }

    @Test
    void convert_withNull_shouldReturnNull() {

        // Act
        String typeArtist = converter.convert(null);

        // Assertions
        Assertions.assertThat(typeArtist).isNull();
    }
}