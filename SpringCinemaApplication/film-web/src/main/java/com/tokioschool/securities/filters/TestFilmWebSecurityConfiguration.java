package com.tokioschool.securities.filters;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad para el perfil de pruebas de la aplicación web de FilmWeb.
 *
 * Esta clase define las cadenas de filtros de seguridad específicas para el entorno de pruebas,
 * incluyendo configuraciones para rutas protegidas, manejo de sesiones y excepciones para la consola H2.
 *
 * Anotaciones utilizadas:
 * - `@Configuration`: Marca esta clase como una clase de configuración de Spring.
 * - `@RequiredArgsConstructor`: Genera un constructor con los argumentos requeridos
 *   para las dependencias inyectadas.
 * - `@Profile("test")`: Indica que esta configuración se aplica únicamente al perfil de pruebas.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
@RequiredArgsConstructor
@Profile("test")
public class TestFilmWebSecurityConfiguration {

    /** Clave utilizada para la funcionalidad de "recordar sesión". */
    @Value("${security.remember-me.key}")
    private String rememberMeKey;

    /**
     * Servicio para cargar los detalles del usuario.
     *
     * Este servicio se utiliza para recuperar información del usuario durante la autenticación.
     */
    @Qualifier("filmUserDetails")
    private final UserDetailsService userDetailsService;

    /**
     * Configuración de la cadena de filtros de seguridad para las rutas bajo "/web/**".
     *
     * Este filtro permite el acceso público a ciertas rutas relacionadas con el registro
     * y edición de usuarios, mientras que requiere autenticación para las demás rutas.
     *
     * @param http Objeto `HttpSecurity` para configurar la seguridad HTTP.
     * @return Una instancia de `SecurityFilterChain` configurada.
     * @throws Exception Si ocurre un error durante la configuración.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/web/**")
                .csrf(Customizer.withDefaults())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/web/users/register", "/web/users/save", "/web/users/edit**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .build();
    }

    /**
     * Configuración de la cadena de filtros de seguridad para las rutas relacionadas con el inicio de sesión.
     *
     * Este filtro permite el acceso público a recursos estáticos y páginas de inicio de sesión,
     * mientras que requiere autenticación para las demás rutas. También configura el manejo de inicio
     * y cierre de sesión, así como la funcionalidad de "recordar sesión". Además, desactiva el CSRF
     * y las opciones de marco para la consola H2.
     *
     * @param http Objeto `HttpSecurity` para configurar la seguridad HTTP.
     * @return Una instancia de `SecurityFilterChain` configurada.
     * @throws Exception Si ocurre un error durante la configuración.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain loginSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .userDetailsService(userDetailsService)
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**")) // Desactiva el CSRF para /h2-console
                .cors(Customizer.withDefaults())
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)) // Requerido para H2
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/login", "/logout").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/web/index", true)
                        .failureUrl("/login?error=true")
                        .usernameParameter("username")
                        .passwordParameter("password"))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll())
                .rememberMe(rememberMe -> rememberMe
                        .key(rememberMeKey)
                        .rememberMeParameter("remember-me")
                        .tokenValiditySeconds(7 * 24 * 60 * 60)
                        .userDetailsService(userDetailsService))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .build();
    }
}