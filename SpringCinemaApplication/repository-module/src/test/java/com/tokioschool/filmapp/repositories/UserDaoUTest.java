package com.tokioschool.filmapp.repositories;

import com.github.javafaker.Faker;
import com.tokioschool.filmapp.domain.User;
import com.tokioschool.filmapp.repositories.configuration.TestConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

@DataJpaTest
@ContextConfiguration(classes = {TestConfig.class,UserDao.class})
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDaoUTest {

    @Autowired private UserDao userDao;
    @Autowired private Faker faker;

    private static List<User> users;

    @BeforeEach
    void init(){
        users =  IntStream.range(0,10).mapToObj(i->{

            String name = faker.name().name();
            Random random = new Random();
            return User.builder()
                    .email("%s@.%s.com".formatted(name,faker.company().suffix()))
                    .name(name)
                    .surname(faker.name().lastName()+i)
                    .username(faker.dragonBall().character())
                    .birthDate(LocalDate.now().minusYears(random.nextLong(10,40)))
                    .created(LocalDateTime.now())
                    .lastLoginAt(LocalDateTime.now())
                    .password("123")
                    .passwordBis("123")
                    .image(null)
                    .build();
        }).toList();

        userDao.saveAll(users);
    }

    @Test
    @Order(1)
    void givenListUsers_whenSave_thenOk() {
        Long count = userDao.count();

        Assertions.assertThat(count).isEqualTo(users.size());
    }

    @Test
    @Order(2)
    void givenUserName_whenFindByUserNameIgnoreCase_thenOk() {
        Optional<User> maybeUser = userDao.findByUsernameIgnoreCase(users.getFirst().getUsername());

        Assertions.assertThat(maybeUser).isNotNull()
                .get()
                .returns(users.getFirst().getName(),User::getName)
                .returns(users.getFirst().getSurname(),User::getSurname);
    }

    @Test
    @Order(3)
    void givenUserName_whenFindByUserNameIgnoreCase_thenEmpty() {
        Optional<User> maybeUser = userDao.findByUsernameIgnoreCase("unknown");

        Assertions.assertThat(maybeUser).isEmpty();
    }

    @Test
    @Order(4)
    void givenEmail_whenFindByEmailIgnoreCase_thenOk() {
        Optional<User> maybeUser = userDao.findByEmailIgnoreCase(users.getFirst().getEmail());

        Assertions.assertThat(maybeUser).isNotNull()
                .get()
                .returns(users.getFirst().getName(),User::getName)
                .returns(users.getFirst().getSurname(),User::getSurname);
    }

    @Test
    @Order(5)
    void givenEmailOrNameUser_whenFindByNameUserOrEmail_thenOk() {
        Optional<User> maybeUser = userDao.findByUsernameOrEmailIgnoreCase(users.getFirst().getEmail());

        Assertions.assertThat(maybeUser).isNotNull()
                .get()
                .returns(users.getFirst().getName(),User::getName)
                .returns(users.getFirst().getSurname(),User::getSurname);
    }
}