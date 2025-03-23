package com.tokioschool.ratingapp.securities.jwt.properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "application.jwt.secret=test-secret",
        "application.jwt.expiration=PT30M"
})
public class JwtAuthConfigurationPropertyUTest {

    @Value("${application.jwt.secret}")
    private String secret;

    @Value("${application.jwt.expiration}")
    private String expiration;

    @Autowired
    JwtConfigurationProperty jwtConfigurationProperty;

    @Test
    void jwtConfigurationProperty_withValidProperties_shouldReturnCorrectValues() {
        Assertions.assertEquals(secret, jwtConfigurationProperty.secret());
        Assertions.assertEquals(Duration.parse(expiration), jwtConfigurationProperty.expiration());
    }

    @Test
    void jwtConfigurationProperty_withZeroExpiration_shouldReturnCorrectValues() {
        JwtConfigurationProperty property = new JwtConfigurationProperty("test-secret", Duration.ZERO);
        Assertions.assertEquals("test-secret", property.secret());
        Assertions.assertEquals(Duration.ZERO, property.expiration());
    }

    // auto load
    @Configuration
    @EnableConfigurationProperties(JwtConfigurationProperty.class)
    protected static class JwtConfigurationPropertyConfigurationTest {
    }
}

