package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.enums.TYPE_ARTIST;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.spi.MappingContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

@ActiveProfiles("test")
public class UUIDToStringConverterUTest {

    private final UUIDToStringConverter converter = new UUIDToStringConverter();

    @Test
    void convert_withValidTypeArtist_shouldReturnNameUpperCase() {

        // Arrange
        UUID uuid = UUID.randomUUID();

        MappingContext<UUID, String> context = Mockito.mock(MappingContext.class);
        Mockito.when(context.getSource()).thenReturn(uuid);

        // Act
        String uuidStr = converter.convert(context);

        // Assertions
        Assertions.assertThat(uuidStr)
                .isEqualTo(uuid.toString());
    }

    @Test
    void convert_withNullUUID_shouldReturnNull() {

        // Arrange
        MappingContext<UUID, String> context = Mockito.mock(MappingContext.class);
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
