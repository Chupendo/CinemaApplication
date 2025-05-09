package com.tokioschool.filmapp.services.security;

import com.tokioschool.filmapp.dto.user.RoleDto;
import com.tokioschool.filmapp.dto.user.UserDto;
import com.tokioschool.filmapp.services.securities.FilmUserDetailsService;
import com.tokioschool.filmapp.services.user.UserService;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class FilmUserDetailsServiceUnitTest {

    @InjectMocks
    private FilmUserDetailsService filmUserDetailsService;

    @Mock
    private UserService userService;

    @Test
    void givenUserName_whenLoadUserByUsername_returnOk() {
        final RoleDto roleDTO = RoleDto.builder().id(1L).name("USER").authorities(List.of("read")).build();
        final UserDto userDto = UserDto.builder()
                .id("1234")
                .name("consumer")
                .username("username")
                .email("email@bla.com")
                .roles(List.of(roleDTO))
                .birthDate(LocalDate.now().minusYears(20))
                .created(LocalDateTime.now())
                .build();
        final String pwd = "123";

        final Optional<Pair<UserDto,String>> maybePairUserDtoAndPwd = Optional.of(Pair.of(userDto,pwd));
        Mockito.when(userService.findUserAndPasswordByEmail("consumer")).thenReturn(maybePairUserDtoAndPwd);

        UserDetails userDetails = filmUserDetailsService.loadUserByUsername("consumer");

        Assertions.assertThat(userDetails)
                .returns(userDto.getEmail(),UserDetails::getUsername)
                .returns(pwd,UserDetails::getPassword)
                .satisfies(userDetails1 ->
                        Assertions.assertThat(userDetails1.getAuthorities()
                                .contains(new SimpleGrantedAuthority("ROLE_"
                                        .concat(userDto.getRoles().getFirst().getName().toUpperCase())))).isTrue() )
        .satisfies(userDetails1 ->
                Assertions.assertThat(userDetails1.getAuthorities()
                        .contains(new SimpleGrantedAuthority(
                                userDto.getRoles().getFirst().getAuthorities().getFirst().toUpperCase()))).isTrue() );
    }
}