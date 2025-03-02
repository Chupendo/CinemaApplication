package com.tokioschool.ratingapp.configurations.mappers;

import com.tokioschool.ratingapp.domains.Role;
import com.tokioschool.ratingapp.domains.User;
import com.tokioschool.ratingapp.dto.users.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
class UserToUserDtoMapperUTest {

    private ModelMapper modelMapper;
    private UserToUserDtoMapper userToUserDtoMapper;

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();
        userToUserDtoMapper = new UserToUserDtoMapper(modelMapper);
    }

    @Test
    void userToUserDtoMapper_convertsUserToUserDto_whenUserHasRoles() {
        User user = new User();
        user.setId("1L");
        user.setUsername("testuser");
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        roles.add(role);
        user.setRoles(roles);

        UserDto userDto = modelMapper.map(user, UserDto.class);

        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo("1L");
        assertThat(userDto.getUsername()).isEqualTo("testuser");
        assertThat(userDto.getRoles()).hasSize(1);
        assertThat(userDto.getRoles().getFirst().getName()).isEqualTo("ROLE_USER");
    }

    @Test
    void userToUserDtoMapper_convertsUserToUserDto_whenUserHasNoRoles() {
        User user = new User();
        user.setId("1L");
        user.setUsername("testuser");
        user.setRoles(Collections.emptySet());

        UserDto userDto = modelMapper.map(user, UserDto.class);

        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo("1L");
        assertThat(userDto.getUsername()).isEqualTo("testuser");
        assertThat(userDto.getRoles()).isEmpty();
    }

    @Test
    void userToUserDtoMapper_returnsNull_whenUserIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> modelMapper.map(null, UserDto.class));
    }
}