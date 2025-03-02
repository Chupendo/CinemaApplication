package com.tokioschool.ratingapp.configurations.convertes;

import com.tokioschool.ratingapp.domains.Role;
import com.tokioschool.ratingapp.dto.roles.RoleDto;
import org.junit.jupiter.api.Test;
import org.modelmapper.spi.MappingContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SetRoleToListRoleDtoConverterUTest {

    private final SetRoleToListRoleDtoConverter converter = new SetRoleToListRoleDtoConverter();

    @Test
    void convert_returnsEmptyList_whenContextIsNull() {
        List<RoleDto> result = converter.convert(null);

        assertThat(result).isEmpty();
    }

    @Test
    void convert_returnsEmptyList_whenSourceSetIsNull() {
        MappingContext<Set<Role>, List<RoleDto>> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(null);

        List<RoleDto> result = converter.convert(context);

        assertThat(result).isEmpty();
    }

    @Test
    void convert_returnsEmptyList_whenSourceSetIsEmpty() {
        MappingContext<Set<Role>, List<RoleDto>> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(Collections.emptySet());

        List<RoleDto> result = converter.convert(context);

        assertThat(result).isEmpty();
    }

    @Test
    void convert_returnsListOfRoleDtos_whenSourceSetIsNotEmpty() {
        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("ROLE_USER");

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("ROLE_ADMIN");

        Set<Role> roles = new HashSet<>();
        roles.add(role1);
        roles.add(role2);

        MappingContext<Set<Role>, List<RoleDto>> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(roles);

        List<RoleDto> result = converter.convert(context);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(RoleDto::getName).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }
}