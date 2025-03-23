package com.tokioschool.ratingapp.securities.configurations.filter;

import com.tokioschool.ratingapp.securities.configurations.handlers.CustomAccessDeniedHandler;
import com.tokioschool.ratingapp.securities.configurations.handlers.CustomAuthenticationEntryPoint;
import com.tokioschool.ratingapp.securities.jwt.converter.CustomJwtAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration class for the Ratings API.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class RatingsApiSecurityConfiguration {

    /**
     *
     * Con "authorizationServer.oidc(...)"
     *
     * Habilita las características de OpenID Connect (OIDC) en el Authorization Server.
     * OpenID Connect extiende OAuth 2.0 y permite la autenticación del usuario, no solo la autorización.
     *
     * Con "Customizer.withDefaults()"
     *
     * Aplica la configuración predeterminada de OIDC sin necesidad de personalizar los endpoints o los claims manualmente.
     * Esto activa automáticamente los endpoints estándar de OIDC, como:
     *  /oauth2/authorize → Para la autenticación y autorización.
     *  /oauth2/token → Para la emisión de tokens de acceso.
     *  /oauth2/introspect → Para validar tokens.
     *  /oauth2/revoke → Para revocar tokens.
     *  /oauth2/jwks → Para la clave pública de validación de tokens.
     *  /.well-known/openid-configuration → Para la metadata de configuración OIDC.
     *
     * @param httpSecurity
     * @return
     * @throws Exception
     */
    @Bean
    @Order(1)
    public SecurityFilterChain oAuth2AuthorizationServerConfigurer(HttpSecurity httpSecurity,AuthorizationServerSettings authorizationServerSettings) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();

        return httpSecurity

                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/oauth2/**", "/.well-known/**").permitAll())
                .with(authorizationServerConfigurer, (authorizationServer) ->
                        authorizationServer
                                .authorizationServerSettings(authorizationServerSettings)
                                .oidc(Customizer.withDefaults())	// Enable OpenID Connect 1.0
                )
                //.userDetailsService()
                .build();
    }

    /**
     * Configures the security filter chain for the Ratings API.
     *
     * @param httpSecurity the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs while configuring the security filter chain
     */
    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChainMainly(HttpSecurity httpSecurity) throws Exception {


        // Define a security chain
        return httpSecurity
                .securityMatcher("/ratings/api/**")
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ratings/api/auth", "/ratings/api/auth/", "ratings/api/auth/login","/ratings/api/auth/authorize").permitAll()
                        .anyRequest().authenticated()
                )
                //.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
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