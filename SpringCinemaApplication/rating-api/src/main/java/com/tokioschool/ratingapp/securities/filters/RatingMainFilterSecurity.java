package com.tokioschool.ratingapp.securities.filters;

import com.tokioschool.jwt.converter.CustomJwtAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.tokioschool.ratingapp.securities.routes.Routes.*;

/**
 * Configuración de seguridad principal para la aplicación de calificaciones.
 *
 * Esta clase configura la cadena de filtros de seguridad, incluyendo autenticación, autorización,
 * manejo de sesiones y protección CSRF. También integra un filtro personalizado para verificar
 * tokens JWT en la lista negra.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class RatingMainFilterSecurity {

    @Qualifier("ratingUserDetails")
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final ClientRegistrationRepository clientRegistrationRepository;

    // Gestión de sesiones
    private final JwtBlackListFilter jwtBlackListFilter;
    private final LogRequestFilter logRequestFilter;

    /**
     * Configura la cadena de filtros de seguridad.
     *
     * @param http Configuración de seguridad HTTP.
     * @param authorizationServerSettings Configuración del servidor de autorización.
     * @return La cadena de filtros de seguridad configurada.
     * @throws Exception Si ocurre un error durante la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthorizationServerSettings authorizationServerSettings) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();

        http
                .userDetailsService(userDetailsService)
                .csrf(csrf -> csrf.ignoringRequestMatchers(H2_CONSOLE_FULL,LOGIN_API_FULL)) // Ignora CSRF en H2 Console
                //.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITE_LIST_URLS).permitAll()
                        .anyRequest().authenticated()
                )
                .with(authorizationServerConfigurer, (authorizationServer) ->
                        authorizationServer
                                //.authorizationServerSettings(AuthorizationServerSettings.builder().build())
                                .authorizationServerSettings(authorizationServerSettings)
                                .oidc(Customizer.withDefaults())    // Enable OpenID Connect 1.0
                )
                //.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())) // Habilita JWT
                //.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(new JwtAuthenticationConverter())))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(new CustomJwtAuthenticationConverter() )))
                .formLogin(Customizer.withDefaults()) // Habilita el form login
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.NEVER)                )
                .logout(Customizer.withDefaults())
                .addFilterAfter(logRequestFilter,UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtBlackListFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)); //required for user wit h2
                //.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)); // Permite iframes de la misma fuente para usar h2


        return http.build();
    }

    /**
     * Configura el proveedor de autenticación.
     *
     * @return El proveedor de autenticación configurado.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
}
