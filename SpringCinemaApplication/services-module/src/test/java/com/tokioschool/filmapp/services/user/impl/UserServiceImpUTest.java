package com.tokioschool.filmapp.services.user.impl;

import com.github.javafaker.Faker;
import com.tokioschool.filmapp.domain.User;
import com.tokioschool.filmapp.dto.user.UserDTO;
import com.tokioschool.filmapp.repositories.UserDao;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

@ExtendWith(MockitoExtension.class)
class UserServiceImpUTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserDao userDao;

    @Spy
    private ModelMapper  modelMapper;

    private static final Faker faker = new Faker();
    private static  List<User> users;


    @BeforeAll
    public static void beforeAll(){
        users = IntStream.range(0,10).mapToObj(i->{
            String name = faker.name().name();
            Random random = new Random();
            return User.builder()
                    .email("%s@.%s.com".formatted(name,faker.company().suffix()))
                    .name(name)
                    .surname(faker.name().lastName())
                    .username(faker.dragonBall().character())
                    .birthDate(LocalDate.now().minusYears(random.nextLong(10,40)))
                    .created(LocalDateTime.now())
                    .lastLoginAt(LocalDateTime.now())
                    .password("123")
                    .passwordBis("123")
                    .build();
        }).toList();
    }

    @Test
    void givenEmail_whenFindUserAndPasswordByEmail_thenReturnUserDto() {
        Mockito.when(userDao.findByUsernameOrEmailIgnoreCase(users.getFirst().getEmail()))
                .thenReturn(Optional.of(users.getFirst()));

        Optional<Pair<UserDTO,String>> maybePairUserDTOPwd = userService.findUserAndPasswordByEmail(users.getFirst().getEmail());

        Mockito.verify(modelMapper,Mockito.times(1)).map(users.getFirst(), UserDTO.class);

        userService.findUserAndPasswordByEmail(users.getFirst().getEmail());

        Assertions.assertThat(maybePairUserDTOPwd).isPresent()
                .get()
                .returns(users.getFirst().getPassword(), Pair::getRight)
                .extracting(Pair::getLeft)
                .isNotNull()
                .returns(users.getFirst().getEmail(),UserDTO::getEmail)
                .returns(users.getFirst().getName(),UserDTO::getName)
                .returns(users.getFirst().getBirthDate(),UserDTO::getBirthDate)
                .returns(users.getFirst().getCreated(),UserDTO::getCreated);
    }

    @Test
    void givenEmail_whenFindUserAndPasswordByUsername_thenReturnUserDto() {
        Mockito.when(userDao.findByUsernameOrEmailIgnoreCase(users.getFirst().getUsername()))
                .thenReturn(Optional.of(users.getFirst()));

        Optional<Pair<UserDTO,String>> maybePairUserDTOPwd = userService.findUserAndPasswordByEmail(users.getFirst().getUsername());

        Mockito.verify(modelMapper,Mockito.times(1)).map(users.getFirst(), UserDTO.class);

        userService.findUserAndPasswordByEmail(users.getFirst().getUsername());

        Assertions.assertThat(maybePairUserDTOPwd).isPresent()
                .get()
                .returns(users.getFirst().getPassword(), Pair::getRight)
                .extracting(Pair::getLeft)
                .isNotNull()
                .returns(users.getFirst().getEmail(),UserDTO::getEmail)
                .returns(users.getFirst().getName(),UserDTO::getName)
                .returns(users.getFirst().getBirthDate(),UserDTO::getBirthDate)
                .returns(users.getFirst().getCreated(),UserDTO::getCreated);
    }

    @Test
    void givenEmail_whenFindByEmail_thenReturnUserDto() {
        Mockito.when(userDao.findByEmailIgnoreCase(users.getFirst().getEmail()))
                .thenReturn(Optional.of(users.getFirst()));

        Optional<UserDTO> maybeUserDTO = userService.findByEmail(users.getFirst().getEmail());

        Mockito.verify(modelMapper,Mockito.times(1)).map(users.getFirst(), UserDTO.class);

        Assertions.assertThat(maybeUserDTO).isPresent().get()
                .returns(users.getFirst().getEmail(),UserDTO::getEmail)
                .returns(users.getFirst().getName(),UserDTO::getName)
                .returns(users.getFirst().getBirthDate(),UserDTO::getBirthDate)
                .returns(users.getFirst().getCreated(),UserDTO::getCreated);
    }
}