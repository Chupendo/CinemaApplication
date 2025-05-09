package com.tokioschool.filmapp.security.filter;

import com.tokioschool.filmapp.security.filter.auth.JwtAuthenticationFilter;
import com.tokioschool.filmapp.security.filter.auth.LogRequestFilter;
import com.tokioschool.filmapp.security.confings.JwtAuthenticationProvider;
import com.tokioschool.jwt.converter.CustomJwtAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

/**
 * Configuración de seguridad para la API de Film.
 *
 * Esta clase define la configuración de seguridad para los endpoints de la API, incluyendo
 * la gestión de autenticación, autorización, sesiones sin estado, y la integración de filtros
 * personalizados para la autenticación JWT y el registro de solicitudes.
 *
 * Anotaciones:
 * - {@link Configuration}: Indica que esta clase es una configuración de Spring.
 * - {@link RequiredArgsConstructor}: Genera un constructor con los argumentos requeridos para los campos finales.
 *
 * Dependencias:
 * - {@link UserDetailsService}: Servicio para cargar los detalles del usuario.
 * - {@link PasswordEncoder}: Codificador de contraseñas para validar credenciales.
 * - {@link JwtAuthenticationProvider}: Proveedor de autenticación para manejar tokens JWT.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
@RequiredArgsConstructor
public class FilmApiSecurityConfiguration {

    /**
     * Servicio para cargar los detalles del usuario.
     *
     * Este servicio se utiliza para recuperar información del usuario durante la autenticación.
     */
    @Qualifier("filmUserDetails")
    private final UserDetailsService userDetailsService;

    /**
     * Codificador de contraseñas.
     *
     * Este componente se utiliza para codificar y validar contraseñas de usuarios.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Proveedor de autenticación JWT.
     *
     * Este componente se utiliza para autenticar usuarios basándose en tokens JWT.
     */
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    /**
     * Configura el administrador de autenticación.
     *
     * Este metodo define un administrador de autenticación que combina la autenticación
     * basada en nombre de usuario y contraseña con la autenticación basada en tokens JWT.
     *
     * @return una instancia de {@link AuthenticationManager}.
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userDetailsService);
        daoProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(List.of(daoProvider, jwtAuthenticationProvider));
    }

    /**
     * Configura la cadena de seguridad para los endpoints de la API.
     *
     * Este metodo define las reglas de seguridad para los endpoints, incluyendo la autorización
     * de solicitudes, la gestión de sesiones sin estado, la desactivación de CSRF, y la integración
     * de filtros personalizados para la autenticación JWT y el registro de solicitudes.
     *
     * @param httpSecurity Configuración de seguridad HTTP.
     * @param jwtFilter Filtro de autenticación JWT.
     * @param logRequestFilter Filtro para registrar información de las solicitudes.
     * @return una instancia de {@link SecurityFilterChain}.
     * @throws Exception si ocurre un error durante la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChainMainly(HttpSecurity httpSecurity,
                                                         JwtAuthenticationFilter jwtFilter,
                                                         LogRequestFilter logRequestFilter
    ) throws Exception {
        return httpSecurity
                .userDetailsService(userDetailsService)
                .securityMatcher("/film/api/**")
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(HttpMethod.POST, "/film/api/auth", "/film/api/auth/", "/film/api/auth/login", "/film/api/users/register")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .oauth2ResourceServer(oAuth2ResourceServerConfigurer -> oAuth2ResourceServerConfigurer.jwt(
                        jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(
                                new CustomJwtAuthenticationConverter()
                        )
                ))
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(logRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}