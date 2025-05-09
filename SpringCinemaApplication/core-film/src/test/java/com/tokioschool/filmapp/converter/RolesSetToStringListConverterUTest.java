package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.domain.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.spi.MappingContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

@ActiveProfiles("test")
class RolesSetToStringListConverterUTest {

    private final RolesSetToStringListConverter converter = new RolesSetToStringListConverter();

    @Test
    void convert_withValidARoleSet_shouldReturnStringList() {
        // Arrange
        Set<Role> roleSet = Set.of(
                Role.builder().id(1L).name("USER").build(),
                Role.builder().id(1L).name("EMPLOYEE").build(),
                Role.builder().id(1L).name("ADMIN").build()
        );

        MappingContext<Set<Role>, List<String>> context = Mockito.mock(MappingContext.class);
        Mockito.when(context.getSource()).thenReturn(roleSet);

        // Act
        List<String> rolesList = converter.convert(context);

        // Assertions
        Assertions.assertThat(rolesList)
                .hasSize(3)
                .isEqualTo(roleSet.stream().map(Role::getName).toList());

    }
    @Test
    void convert_withNullRoleSet_shouldReturnListEmpty() {

        MappingContext<Set<Role>, List<String>> context = Mockito.mock(MappingContext.class);
        Mockito.when(context.getSource()).thenReturn(null);

        // Act
        List<String> rolesList = converter.convert(context);

        // Assertions
        Assertions.assertThat(rolesList)
                .isEmpty();
    }

    @Test
    void convert_withNull_shouldReturnListEmpty() {

        // Act
        List<String> rolesList = converter.convert(null);

        // Assertions
        Assertions.assertThat(rolesList)
                .isEmpty();
    }
}