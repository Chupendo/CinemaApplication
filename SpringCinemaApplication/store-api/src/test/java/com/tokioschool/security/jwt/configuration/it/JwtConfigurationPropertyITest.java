package com.tokioschool.security.jwt.configuration.it;

import com.tokioschool.storeapp.security.jwt.properties.JwtConfigurationProperty;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes={
        JwtConfigurationPropertyITest.JwtConfigurationTest.class
})
class JwtConfigurationPropertyITest {

    @Autowired
    JwtConfigurationProperty jwtConfigurationProperty;

    @Test
    void givenContextTextSpring_whenLoadProperties_thenReadJwtProperties() {

        String secret = jwtConfigurationProperty.secret();
        Duration expiredAt = jwtConfigurationProperty.expiration();

        Assertions.assertThat(secret).isNotNull();
        Assertions.assertThat(expiredAt)
                .isNotNull()
                .returns(Duration.ofHours(1).toHours(),Duration::toHours);
    }

    @Configuration
    @EnableConfigurationProperties(JwtConfigurationProperty.class)
    protected static class JwtConfigurationTest {
    }
}