package com.tokioschool.storeapp.security.filter;

import com.tokioschool.storeapp.security.jwt.converter.CustomJwtAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class StoreApiSecurityConfiguration {

    public final JwtBlackListFilter jwtBlackListFilter;

    @Bean
    public SecurityFilterChain securityFilterChainMainly(HttpSecurity httpSecurity) throws Exception {
        // define una cadena de seugridad
        return httpSecurity
                // gestion de securizar los endpoints
                .securityMatcher("/store/api/**")
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        //.requestMatchers("/store/api/auth","/store/api/auth/**")
                        .requestMatchers("/store/api/auth")
                        .permitAll()
                        .requestMatchers("/store/api/auth/me").hasRole("ADMIN")
                        .requestMatchers("/store/api/**")
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
                .oauth2ResourceServer(oAuth2ResourceServerConfigurer -> oAuth2ResourceServerConfigurer.jwt(
                        jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(
                                new CustomJwtAuthenticationConverter()
                        )
                ))
                // add filter before authentication
                .addFilterBefore(jwtBlackListFilter, UsernamePasswordAuthenticationFilter.class)
         .build();
    }
}
