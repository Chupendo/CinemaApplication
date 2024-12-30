package com.tokioschool.filmapp.mapper;

import com.tokioschool.filmapp.domain.User;
import com.tokioschool.filmapp.dto.user.UserDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ModelMapperConfiguration.class})
class ModelMapperUTest {

    @Autowired  private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    void givenUser_whenMapperToUserDto_whenUserDto(){
        User user = User.builder()
                .name("andres")
                .surname("ruiz peñuela")
                .username("arp0001")
                .created(LocalDateTime.now())
                .lastLoginAt(LocalDateTime.now())
                .birthDate(LocalDate.now().minusYears(32))
                .password("123")
                .passwordBis("123")
                .email("test@test.com")
                .build();


        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        Assertions.assertThat(userDTO)
                .returns(user.getName(),UserDTO::getName);
    }
}