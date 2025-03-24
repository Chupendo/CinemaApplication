package com.tokioschool.ratingapp.securities.filters;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import static com.tokioschool.ratingapp.securities.routes.Routes.WHITE_LIST_URLS;

@Configuration
@EnableWebSecurity
public class RatingMainFilterSecurity {

    @Bean
    public  SecurityFilterChain securityFilterChain(HttpSecurity http, AuthorizationServerSettings authorizationServerSettings) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        //.anyRequest().permitAll()
                        .requestMatchers(WHITE_LIST_URLS).permitAll()
                        .anyRequest().authenticated()
                )
                .with(authorizationServerConfigurer, (authorizationServer) ->
                        authorizationServer
                                .authorizationServerSettings(authorizationServerSettings)
                                .oidc(Customizer.withDefaults())	// Enable OpenID Connect 1.0
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(new JwtAuthenticationConverter())))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)); //required for user wit h2

        return http.build();
    }
}
