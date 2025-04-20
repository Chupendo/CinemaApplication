package com.tokioschool.securities.filters;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class FilmWebSecurityConfiguration {

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
     * Codificador de contraseñas.
     *
     * Este componente se utiliza para codificar y validar contraseñas de usuarios.
     */
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Order(1)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(Customizer.withDefaults())
                .cors(Customizer.withDefaults())
                .securityMatcher("/web/**")
                .authorizeHttpRequests(auth -> auth.
                        requestMatchers("/web/users/register","/web/users/save").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain loginSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .userDetailsService(userDetailsService)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/login", "/logout").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/web/index", true)
                        .failureUrl("/login?error=true")
                        .usernameParameter("username")
                        .passwordParameter("password")
                )
                .logout(logoutCustomer ->
                        logoutCustomer
                                .logoutUrl("/logout") // URL para hacer logout
                                .logoutSuccessUrl("/login?logout=true") // Redirección después de logout
                                .deleteCookies("JSESSIONID", "remember-me") // Eliminar cookies de sesión y remember-me
                                .permitAll() // Permite que cualquier usuario acceda a la página de logout
                )
                .rememberMe(rememberMe -> rememberMe
                        .key(rememberMeKey)
                        .rememberMeParameter("remember-me")  // el name del checkbox en el form
                        .tokenValiditySeconds(7 * 24 * 60 * 60) // 7 días
                        .userDetailsService(userDetailsService)
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .build();
    }

}
