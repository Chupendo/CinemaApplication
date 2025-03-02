package com.tokioschool.ratingapp.configurations.mappers;


import com.tokioschool.ratingapp.domains.Authority;
import com.tokioschool.ratingapp.domains.Role;
import com.tokioschool.ratingapp.dto.roles.RoleDto;
import com.tokioschool.ratingapp.dto.users.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


class RoleToRoleDtoMapperUTest {

    private ModelMapper modelMapper;
    private RoleToRoleDtoMapper roleToRoleDtoMapper;

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();
        roleToRoleDtoMapper = new RoleToRoleDtoMapper(modelMapper);
    }

    @Test
    void roleToRoleDtoMapper_convertsRoleToRoleDto_whenRoleHasAuthorities() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        Set<Authority> authorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setId(1L);
        authority.setName("READ_PRIVILEGE");
        authorities.add(authority);
        role.setAuthorities(authorities);

        RoleDto roleDto = modelMapper.map(role, RoleDto.class);

        assertThat(roleDto).isNotNull();
        assertThat(roleDto.getId()).isEqualTo(1L);
        assertThat(roleDto.getName()).isEqualTo("ROLE_USER");
        assertThat(roleDto.getAuthorities()).hasSize(1);
        assertThat(roleDto.getAuthorities().get(0).getName()).isEqualTo("READ_PRIVILEGE");
    }

    @Test
    void roleToRoleDtoMapper_convertsRoleToRoleDto_whenRoleHasNoAuthorities() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        role.setAuthorities(Collections.emptySet());

        RoleDto roleDto = modelMapper.map(role, RoleDto.class);

        assertThat(roleDto).isNotNull();
        assertThat(roleDto.getId()).isEqualTo(1L);
        assertThat(roleDto.getName()).isEqualTo("ROLE_USER");
        assertThat(roleDto.getAuthorities()).isEmpty();
    }

    @Test
    void roleToRoleDtoMapper_returnsNull_whenRoleIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> modelMapper.map(null, UserDto.class));

    }
}