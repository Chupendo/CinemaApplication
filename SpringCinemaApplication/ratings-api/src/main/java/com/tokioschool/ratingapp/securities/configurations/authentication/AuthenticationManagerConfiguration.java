package com.tokioschool.ratingapp.securities.configurations.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

/**
 * Configuration class for setting up the AuthenticationManager bean.
 */
@Configuration
public class AuthenticationManagerConfiguration {

    /**
     * Creates an AuthenticationManager bean.
     * This bean is automatically injected if "formLogin" is used in the filter chain.
     *
     * @param authenticationConfiguration the authentication configuration
     * @return the authentication manager
     * @throws Exception if an error occurs while getting the authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
