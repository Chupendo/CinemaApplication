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
    void givenSetRoles_wneConvert_tenReturnListRolesDTO() {
        // Arrange


        MappingContext<TYPE_ARTIST, String> context = Mockito.mock(MappingContext.class);
        Mockito.when(context.getSource()).thenReturn(TYPE_ARTIST.DIRECTOR);

        // Act
        String typeArtist = converter.convert(context);

        // Assertions
        Assertions.assertThat(typeArtist)
                .isEqualTo("DIRECTOR");

    }
}