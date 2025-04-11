package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.domain.Authority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.spi.MappingContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
public class AuthoritySetToStringListConverterUTest {

    AuthoritySetToStringListConverter converter;

    @BeforeEach
    void setUp() {
        converter = new AuthoritySetToStringListConverter();
    }

    @Test
    void convert_withValidAuthoritySet_shouldReturnStringList() {
        Set<Authority> authoritySet = Set.of(
                Authority.builder().id(1L).name("ROLE_USER").build(),
                Authority.builder().id(2L).name("ROLE_ADMIN").build()
        );

        MappingContext<Set<Authority>, List<String>> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(authoritySet);

        List<String> result = converter.convert(context);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }


    @Test
    void convert_withNullArtist_shouldReturnEmptyList() {
        MappingContext<Set<Authority>, List<String>> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(null);

        List<String> result = converter.convert(context);

        assertThat(result).isEmpty();
    }

    @Test
    void convert_withNullNull_shouldReturnEmptyList() {

        List<String> result = converter.convert(null);

        assertThat(result).isEmpty();
    }
}
