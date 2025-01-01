package com.tokioschool.filmapp.security.filter;

import com.tokioschool.filmapp.jwt.converter.CustomJwtAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class FilmApiSecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChainMainly(HttpSecurity httpSecurity) throws Exception {
        // define una cadena de seugridad
        return httpSecurity
                // gestion de securizar los endpoints
                .securityMatcher("/film/api/**")
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        //.requestMatchers("/store/api/auth","/store/api/auth/**")
                        .requestMatchers("/film/api/auth")
                        .permitAll()
                        .requestMatchers("/film/api/auth/**")
                        .authenticated()
                )
                // Gestion de session sin estado
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.
                                sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // se desabilita el csrf, al no generar formualiros
                .csrf(AbstractHttpConfigurer::disable)
                // corfs por defecto para recibir petionces del localhost
                .cors(Customizer.withDefaults())
                // validacion de jwt (decodifcador)
                //.oauth2ResourceServer(oAuth2ResourceServerConfigurer -> oAuth2ResourceServerConfigurer.jwt(Customizer.withDefaults()))
                .oauth2ResourceServer(oAuth2ResourceServerConfigurer -> oAuth2ResourceServerConfigurer.jwt(
                        jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(
                                new CustomJwtAuthenticationConverter()
                        )
                ))
         .build();
    }
}
