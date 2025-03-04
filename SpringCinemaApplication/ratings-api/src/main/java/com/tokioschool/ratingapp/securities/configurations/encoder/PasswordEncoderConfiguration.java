package com.tokioschool.ratingapp.securities.configurations.encoder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for setting up the PasswordEncoder bean.
 */
@Configuration
public class PasswordEncoderConfiguration {

    /**
     * Creates a PasswordEncoder bean.
     * This bean is used to encode passwords using the BCrypt hashing algorithm.
     *
     * @return the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
