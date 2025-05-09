package com.tokioschool.userdetails.configuration.it;

import com.tokioschool.storeapp.userdetails.service.StoreUserDetailsInMemoryServiceConfiguration;
import com.tokioschool.storeapp.userdetails.properties.StoreUserConfigurationProperty;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;


@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        StoreUserDetailsInMemoryServiceConfiguration.class,
        StoreUserDtoDetailsInMemoryServiceConfigurationITest.StoreUserConfigurationTest.class
})
class StoreUserDtoDetailsInMemoryServiceConfigurationITest {

    @Autowired
    private UserDetailsService userDetailsService;


    @Test
    void whenLoadUserByUsername_thenUserIsReturned() {
        UserDetails user1 = userDetailsService.loadUserByUsername("user");
        Assertions.assertThat(user1).isNotNull();
        Assertions.assertThat(user1.getUsername()).isEqualTo("user");
        Assertions.assertThat(user1.getAuthorities()).hasSize(2); // 1 rol + 1 autoridad

        UserDetails admin = userDetailsService.loadUserByUsername("admin");
        Assertions.assertThat(admin).isNotNull();
        Assertions.assertThat(admin.getUsername()).isEqualTo("admin");
        Assertions.assertThat(admin.getAuthorities()).hasSize(4); // 2 rol + 2 autoridad
    }

    @Test
    void whenLoadUserByUsername_withInvalidUsername_thenExceptionIsThrown() {
        org.junit.jupiter.api.Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("invalidUser");
        });
    }

    @Configuration
    @EnableConfigurationProperties(StoreUserConfigurationProperty.class)
    protected static class StoreUserConfigurationTest {
    }
}