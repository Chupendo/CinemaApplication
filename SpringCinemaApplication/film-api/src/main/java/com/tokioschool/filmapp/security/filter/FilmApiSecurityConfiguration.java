package com.tokioschool.filmapp.security.filter;

import com.tokioschool.filmapp.core.filter.LogRequestFilter;
import com.tokioschool.filmapp.jwt.converter.CustomJwtAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class FilmApiSecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final LogRequestFilter logRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChainMainly(HttpSecurity httpSecurity) throws Exception {
        // define una cadena de seugridad
        return httpSecurity
                // gestion de securizar los endpoints
                .securityMatcher("/film/api/**")
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(HttpMethod.POST,"/film/api/auth","/film/api/auth/","/film/api/auth/login","/film/api/users/register")
                                .permitAll()
                        .anyRequest()
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
                // validacion de jwt (decodifcador), considerando los roles
                //.oauth2ResourceServer(oAuth2ResourceServerConfigurer -> oAuth2ResourceServerConfigurer.jwt(Customizer.withDefaults()))
                .oauth2ResourceServer(oAuth2ResourceServerConfigurer -> oAuth2ResourceServerConfigurer.jwt(
                        jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(
                                new CustomJwtAuthenticationConverter()
                        )
                ))
                // login and logout
                .formLogin(AbstractHttpConfigurer::disable)
                //.logout(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer.logoutUrl("/film/api/auth/logout").permitAll())
                // filters
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(logRequestFilter,UsernamePasswordAuthenticationFilter.class)
         .build();
    }
}
