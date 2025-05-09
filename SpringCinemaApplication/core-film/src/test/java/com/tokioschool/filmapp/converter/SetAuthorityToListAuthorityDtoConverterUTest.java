package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.domain.Authority;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.spi.MappingContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

@ActiveProfiles("test")
public class SetAuthorityToListAuthorityDtoConverterUTest {

    private SetAuthorityToListAuthorityDtoConverter converter = new SetAuthorityToListAuthorityDtoConverter();

    @Test
    void convert_withValidAuthoritySet_shouldReturnAuthorityDtoList(){
        Set<Authority> authoritySet = Set.of(
                Authority.builder().id(1L).name("read").build(),
                Authority.builder().id(2L).name("write").build()
        );

        MappingContext< Set<Authority>, List<String> > context = Mockito.mock(MappingContext.class);
        Mockito.when(context.getSource()).thenReturn(authoritySet);

        // Act
        List<String> result = converter.convert(context);

        // Assertions
        Assertions.assertThat(result).hasSize(2)
                .containsExactlyInAnyOrder("read", "write");
    }

    @Test
    void convert_withValidNullAuthoritySet_shouldReturnListEmpty(){

        MappingContext< Set<Authority>, List<String> > context = Mockito.mock(MappingContext.class);
        Mockito.when(context.getSource()).thenReturn(null);

        // Act
        List<String> result = converter.convert(context);

        // Assertions
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void convert_withNull_shouldReturnListEmpty(){

        // Act
        List<String> result = converter.convert(null);

        // Assertions
        Assertions.assertThat(result).isEmpty();
    }
}
