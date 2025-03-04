package com.tokioschool.ratingapp.securities.configurations.encoder;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
class PasswordEncoderConfigurationITest {

    private static  PasswordEncoderConfiguration passwordEncoderConfiguration;

    @BeforeAll
    public static void init(){
        passwordEncoderConfiguration = new PasswordEncoderConfiguration();
    }

    @Test
    void passwordEncoder_returnsBCryptPasswordEncoderInstance() {
        PasswordEncoder passwordEncoder = passwordEncoderConfiguration.passwordEncoder();

        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    void passwordEncoder_encodesPasswordCorrectly() {
        PasswordEncoder passwordEncoder = passwordEncoderConfiguration.passwordEncoder();
        String rawPassword = "password";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }
}