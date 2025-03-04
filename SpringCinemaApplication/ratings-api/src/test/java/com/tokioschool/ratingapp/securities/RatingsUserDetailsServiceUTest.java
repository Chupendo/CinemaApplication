package com.tokioschool.ratingapp.securities;

import com.tokioschool.ratingapp.dto.roles.RoleDto;
import com.tokioschool.ratingapp.dto.users.UserDto;
import com.tokioschool.ratingapp.services.UserService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class RatingsUserDetailsServiceUTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private RatingsUserDetailsService ratingsUserDetailsService;

    @Test
    void loadUserByUsername_returnsUserDetails_whenUsernameOrEmailMatches() {
        String usernameOrEmail = "testuser@test.com";
        UserDto userDto = new UserDto();
        userDto.setEmail(usernameOrEmail);
        userDto.setRoles(Collections.singletonList( RoleDto.builder().name("USER").build() ));

        when(userService.findUserAndPasswordByEmail(usernameOrEmail)).thenReturn(Optional.of(Pair.of(userDto, "password")));

        UserDetails userDetails = ratingsUserDetailsService.loadUserByUsername(usernameOrEmail);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(usernameOrEmail);
        assertThat(userDetails.getPassword()).isEqualTo("password");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
    }

    @Test
    void loadUserByUsername_throwsUsernameNotFoundException_whenUsernameOrEmailNotFound() {
        String usernameOrEmail = "nonexistent@test.com";
        when(userService.findUserAndPasswordByEmail(usernameOrEmail)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> ratingsUserDetailsService.loadUserByUsername(usernameOrEmail));
    }

    @Test
    void loadUserByUsername_throwsUsernameNotFoundException_whenUsernameOrEmailIsNull() {
        assertThrows(UsernameNotFoundException.class, () -> ratingsUserDetailsService.loadUserByUsername(null));
    }

    @Test
    void loadUserByUsername_throwsUsernameNotFoundException_whenUserServiceThrowsIllegalArgumentException() {
        String usernameOrEmail = "invalid@test.com";
        when(userService.findUserAndPasswordByEmail(usernameOrEmail)).thenThrow(IllegalArgumentException.class);

        assertThrows(UsernameNotFoundException.class, () -> ratingsUserDetailsService.loadUserByUsername(usernameOrEmail));
    }
}