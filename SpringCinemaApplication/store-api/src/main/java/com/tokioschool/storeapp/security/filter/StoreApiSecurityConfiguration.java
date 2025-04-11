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

/**
 * Configuración de seguridad para la API de la tienda.
 *
 * Esta clase define la configuración de seguridad para los endpoints de la API de la tienda,
 * incluyendo la gestión de sesiones, autorización de solicitudes y validación de tokens JWT.
 *
 * Notas:
 * - Utiliza `SecurityFilterChain` para configurar la seguridad de los endpoints.
 * - Aplica un filtro personalizado para verificar si los tokens JWT están en la lista negra.
 * - Configura la política de sesión como sin estado (stateless).
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
@RequiredArgsConstructor
public class StoreApiSecurityConfiguration {

    /**
     * Filtro personalizado para verificar si un token JWT está en la lista negra.
     */
    public final JwtBlackListFilter jwtBlackListFilter;

    /**
     * Define la cadena de seguridad para los endpoints de la API de la tienda.
     *
     * Este metodo configura la seguridad de los endpoints bajo el patrón `/store/api/**`,
     * incluyendo la autorización de solicitudes, la gestión de sesiones sin estado,
     * la desactivación de CSRF y la validación de tokens JWT.
     *
     * @param httpSecurity Objeto `HttpSecurity` proporcionado por Spring Security para configurar la seguridad.
     * @return Una instancia de `SecurityFilterChain` con la configuración aplicada.
     * @throws Exception Si ocurre un error durante la configuración de seguridad.
     */
    @Bean
    public SecurityFilterChain securityFilterChainMainly(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // Define los endpoints que serán protegidos por esta configuración
                .securityMatcher("/store/api/**")
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        // Permite el acceso público a los endpoints de autenticación
                        .requestMatchers("/store/api/auth")
                        .permitAll()
                        // Restringe el acceso al endpoint "/store/api/auth/me" solo a usuarios con el rol ADMIN
                        .requestMatchers("/store/api/auth/me").hasRole("ADMIN")
                        // Requiere autenticación para cualquier otro endpoint bajo "/store/api/**"
                        .requestMatchers("/store/api/**")
                        .authenticated()
                )
                // Configura la política de sesión como sin estado
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Desactiva la protección CSRF, ya que no se generan formularios
                .csrf(AbstractHttpConfigurer::disable)
                // Configura CORS con valores predeterminados
                .cors(Customizer.withDefaults())
                // Configura la validación de tokens JWT con un convertidor personalizado
                .oauth2ResourceServer(oAuth2ResourceServerConfigurer -> oAuth2ResourceServerConfigurer.jwt(
                        jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(
                                new CustomJwtAuthenticationConverter()
                        )
                ))
                // Agrega el filtro personalizado antes del filtro de autenticación
                .addFilterBefore(jwtBlackListFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}