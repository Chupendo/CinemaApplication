package com.tokioschool.ratingapp.securities.configurations.filter;

import com.tokioschool.ratingapp.securities.configurations.handlers.CustomAccessDeniedHandler;
import com.tokioschool.ratingapp.securities.configurations.handlers.CustomAuthenticationEntryPoint;
import com.tokioschool.ratingapp.securities.jwt.converter.CustomJwtAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration class for the Ratings API.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class RatingsApiSecurityConfiguration {

    /**
     * Configures the security filter chain for the Ratings API.
     *
     * @param httpSecurity the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs while configuring the security filter chain
     */
    @Bean
    public SecurityFilterChain securityFilterChainMainly(HttpSecurity httpSecurity) throws Exception {
        // Define a security chain
        return httpSecurity
                .securityMatcher("/ratings/api/**")
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ratings/api/auth", "/ratings/api/auth/", "ratings/api/auth/login").permitAll()
                        .requestMatchers("ratings/api/auth/me").hasRole("ADMIN")
                )
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                // Stateless session management
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Disable CSRF as no forms are generated
                .csrf(AbstractHttpConfigurer::disable)
                // Default CORS configuration to allow requests from localhost
                .cors(Customizer.withDefaults())
                // Basic authentication
                .httpBasic(Customizer.withDefaults())
                // JWT validation (decoder), considering roles
                .oauth2ResourceServer(oAuth2ResourceServerConfigurer -> oAuth2ResourceServerConfigurer.jwt(
                        jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(
                                new CustomJwtAuthenticationConverter()
                        )
                ))
                // Disable Spring's form login
                .formLogin(AbstractHttpConfigurer::disable)
                .build();
    }
}