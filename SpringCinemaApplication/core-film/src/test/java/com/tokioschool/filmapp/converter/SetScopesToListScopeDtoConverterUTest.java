package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.domain.Scope;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.spi.MappingContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

@ActiveProfiles("test")
public class SetScopesToListScopeDtoConverterUTest {

    private SetScopesToListScopeDtoConverter converter = new SetScopesToListScopeDtoConverter();

    @Test
    void convert_withValidScopesSet_shouldReturnScopesList(){
        Set<Scope> roleSet = Set.of(
                Scope.builder().id(1L).name("openid").build(),
                Scope.builder().id(2L).name("read").build()
        );

        MappingContext< Set<Scope>, List<String>> context = Mockito.mock(MappingContext.class);
        Mockito.when(context.getSource()).thenReturn(roleSet);

        List<String> result = converter.convert(context);

        Assertions.assertThat(result).hasSize(2)
                .containsExactlyInAnyOrder("openid", "read");

    }
    @Test
    void convert_withValidNullAuthoritySet_shouldReturnListEmpty(){

        MappingContext< Set<Scope>, List<String>> context = Mockito.mock(MappingContext.class);
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
