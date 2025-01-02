package com.tokioschool.filmapp.mapper;

import com.tokioschool.filmapp.domain.Role;
import com.tokioschool.filmapp.domain.User;
import com.tokioschool.filmapp.dto.user.UserDTO;
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

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ModelMapperConfiguration.class,UserToUserDtoMapper.class})
@ActiveProfiles("test")
public class UserToUserDtoMapperUTest {

    @Autowired
    private ModelMapper modelMapper;

    @Test
    void givenUser_whenMapperToUserDto_whenUserDto(){
        final Role role = Role.builder().id(1L).name("ADMIN").build();
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
                .build();


        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        Assertions.assertThat(userDTO)
                .returns(user.getName(),UserDTO::getName)
                .returns(user.getEmail(),UserDTO::getEmail)
                .returns(user.getBirthDate(),UserDTO::getBirthDate)
                .satisfies(userDTO1 ->
                        Assertions.assertThat(userDTO1.getRoles().contains( role.getName() )).isTrue() );
    }
}
