package com.tokioschool.filmapp.mapper;

import com.tokioschool.configs.ModelMapperConfiguration;
import com.tokioschool.filmapp.domain.Authority;
import com.tokioschool.filmapp.domain.Role;
import com.tokioschool.filmapp.domain.Scope;
import com.tokioschool.filmapp.domain.User;
import com.tokioschool.filmapp.dto.user.UserDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ModelMapperConfiguration.class,UserToUserDtoMapper.class})
@ActiveProfiles("test")
public class UserToUserDtoMapperUTest {

    @Autowired
    private ModelMapper modelMapper;

    @Test
    void givenUser_whenMapperToUserDto_whenUserDto(){
        final UUID resourceId = UUID.randomUUID();

        final Role role = Role.builder()
                .id(1L)
                .name("ADMIN")
                .authorities(Set.of(Authority.builder().id(1L).name("writer").build()))
                .scopes(Set.of(Scope.builder().id(1L).name("openid").build()))
                .build();

        final User user = User.builder()
                .name("andres")
                .surname("ruiz peñuela")
                .username("arp0001")
                .created(LocalDateTime.now())
                .lastLoginAt(LocalDateTime.now())
                .birthDate(LocalDate.now().minusYears(32))
                .password("123")
                .passwordBis("123")
                .email("test@test.com")
                .roles(Set.of(role))
                .image(resourceId)
                .build();


        UserDto userDTO = modelMapper.map(user, UserDto.class);

        Assertions.assertThat(userDTO)
                .returns(user.getName(), UserDto::getName)
                .returns(user.getEmail(), UserDto::getEmail)
                .returns(user.getBirthDate(), UserDto::getBirthDate)
                .returns(user.getLastLoginAt(), UserDto::getLastLogin)
                .returns(user.getCreated(), UserDto::getCreated)
                .returns(user.getUsername(), UserDto::getUsername)
                .returns(user.getSurname(), UserDto::getSurname)
                .returns(user.getImage().toString(), UserDto::getResourceId)
                .satisfies(userDTO1 ->
                        Assertions.assertThat(userDTO1.getRoles().getFirst().getAuthorities().contains( "writer" )).isTrue() )
                .satisfies(userDTO1 ->
                        Assertions.assertThat(userDTO1.getRoles().getFirst().getScopes().contains( "openid" )).isTrue() );
    }
}
