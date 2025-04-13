package com.tokioschool.filmapp.mapper;

import com.tokioschool.configs.ModelMapperConfiguration;
import com.tokioschool.filmapp.domain.Authority;
import com.tokioschool.filmapp.domain.Role;
import com.tokioschool.filmapp.domain.Scope;
import com.tokioschool.filmapp.dto.user.RoleDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ModelMapperConfiguration.class,RoleToRoleDtoMapper.class})
@ActiveProfiles("test")
class RoleToRoleDtoMapperUTest {

    @Autowired
    private ModelMapper modelMapper;

    @Test
    void givenUser_whenMapperToUserDto_whenUserDto() {

        final Role role = Role.builder()
                .id(1L)
                .name("ADMIN")
                .authorities(Set.of(Authority.builder().id(1L).name("writer").build()))
                .scopes(Set.of(Scope.builder().id(1L).name("openid").build()))
                .build();

        final RoleDto roleDto = modelMapper.map(role,RoleDto.class);

        Assertions.assertThat(roleDto)
                .returns(role.getName(),RoleDto::getName)
                .satisfies(roleDto1 -> roleDto1.getAuthorities().getFirst().contains("writer"))
                .satisfies(roleDto1 -> roleDto1.getScopes().getFirst().contains("openid"));

    }
}