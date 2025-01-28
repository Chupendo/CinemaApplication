package com.tokioschool.userdetails.configuration.it;

import com.tokioschool.storeapp.userdetails.dto.UserDto;
import com.tokioschool.storeapp.userdetails.properties.StoreUserConfigurationProperty;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;


@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes={
        StoreUserDtoConfigurationPropertyITest.StoreUserConfigurationTest.class
})
class StoreUserDtoConfigurationPropertyITest {

    @Autowired
    StoreUserConfigurationProperty storeUserConfigurationProperty;

    @Test
    void givenContextTextSpring_whenLoadProperties_thenReadUsersStore() {

        List<UserDto> usersInMemory = storeUserConfigurationProperty.users();

        Assertions.assertThat(usersInMemory).isNotEmpty().hasSize(2);
    }

    @Configuration
    @EnableConfigurationProperties(StoreUserConfigurationProperty.class)
    protected static class StoreUserConfigurationTest {
    }

}