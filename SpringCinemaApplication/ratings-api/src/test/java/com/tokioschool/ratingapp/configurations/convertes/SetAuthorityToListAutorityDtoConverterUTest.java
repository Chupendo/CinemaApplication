package com.tokioschool.ratingapp.configurations.convertes;

import com.tokioschool.ratingapp.domains.Authority;
import com.tokioschool.ratingapp.dto.authorities.AuthorityDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.spi.MappingContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SetAuthorityToListAuthorityDtoConverterUTest {

    private final SetAuthorityToListAuthorityDtoConverter converter = new SetAuthorityToListAuthorityDtoConverter();

    @Test
    void convert_returnsEmptyList_whenContextIsNull() {
        List<AuthorityDto> result = converter.convert(null);

        assertThat(result).isEmpty();
    }

    @Test
    void convert_returnsEmptyList_whenSourceSetIsNull() {
       MappingContext<Set<Authority>, List<AuthorityDto>> context = Mockito.mock(MappingContext.class);
        Mockito.when(context.getSource()).thenReturn(null);

        List<AuthorityDto> result = converter.convert(context);

        assertThat(result).isEmpty();
    }

    @Test
    void convert_returnsEmptyList_whenSourceSetIsEmpty() {
        MappingContext<Set<Authority>, List<AuthorityDto>> context = Mockito.mock(MappingContext.class);
        Mockito.when(context.getSource()).thenReturn(Collections.emptySet());

        List<AuthorityDto> result = converter.convert(context);

        assertThat(result).isEmpty();
    }

    @Test
    void convert_returnsListOfAuthorityDtos_whenSourceSetIsNotEmpty() {
        Authority authority1 = new Authority();
        authority1.setId(1L);
        authority1.setName("ROLE_USER");

        Authority authority2 = new Authority();
        authority2.setId(2L);
        authority2.setName("ROLE_ADMIN");

        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority1);
        authorities.add(authority2);

        MappingContext<Set<Authority>, List<AuthorityDto>> context = Mockito.mock(MappingContext.class);
        Mockito.when(context.getSource()).thenReturn(authorities);

        List<AuthorityDto> result = converter.convert(context);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(AuthorityDto::getName).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }
}