package com.tokioschool.filmapp.repositories;


import com.github.javafaker.Faker;
import com.tokioschool.filmapp.domain.Role;
import com.tokioschool.filmapp.repositories.configuration.TestConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@DataJpaTest
@ContextConfiguration(classes = {TestConfig.class,RoleDao.class})
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RoleDaoUTest {

    @Autowired private RoleDao roleDao;
    @Autowired private Faker faker;

    List<Role> roles;

    @BeforeEach
    void init(){
        List<String> nameUsed = new ArrayList<>();
        roles = IntStream.range(0,10).mapToObj(i->{

            nameUsed.add(i, nameIsUsed( nameUsed ));

            return Role.builder()
                    .name( nameUsed.get(i) )
                    .build();
        }).toList();

        roleDao.saveAll(roles);
    }

    @Test
    @Order(1)
    void givenListRoles_whenSave_thenOk() {
        Long count = roleDao.count();

        Assertions.assertThat(count).isEqualTo(roles.size());
    }

    @Test
    void givenRoleName_whenFindByName_thenOk(){
        Role role = roleDao.findByNameIgnoreCase(roles.getFirst().getName());

        Assertions.assertThat(role)
                .returns(roles.getFirst().getName(),Role::getName);
    }

    private String nameIsUsed(List<String> names){
        String maybeName;
        do {
            maybeName = faker.name().name();
        }while ( names.contains( maybeName ));

        return maybeName;
    }
}
