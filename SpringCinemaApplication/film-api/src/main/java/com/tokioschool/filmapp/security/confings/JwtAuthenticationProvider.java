package com.tokioschool.filmapp.security.confings;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Proveedor de autenticación personalizado para manejar tokens JWT.
 *
 * Este componente implementa la interfaz {@link AuthenticationProvider} y se encarga de autenticar
 * usuarios basándose en tokens JWT. Valida el token, decodifica su contenido y recupera los detalles
 * del usuario para crear un objeto de autenticación.
 *
 * Anotaciones:
 * - {@link Component}: Marca esta clase como un componente de Spring para que pueda ser detectada
 *   automáticamente durante el escaneo de componentes.
 * - {@link RequiredArgsConstructor}: Genera un constructor con los argumentos requeridos para los
 *   campos finales.
 *
 * Dependencias:
 * - {@link UserDetailsService}: Servicio para cargar los detalles del usuario.
 * - {@link JwtDecoder}: Decodificador de tokens JWT.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    /**
     * Servicio para cargar los detalles del usuario.
     *
     * Este servicio se utiliza para recuperar información del usuario a partir del nombre de usuario
     * extraído del token JWT.
     */
    @Qualifier("filmUserDetails")
    private final UserDetailsService userDetailsService;

    /**
     * Decodificador de JWT.
     *
     * Este componente se utiliza para validar y decodificar el token JWT proporcionado.
     */
    private final JwtDecoder jwtDecoder;

    /**
     * Autentica un usuario basado en el token JWT proporcionado.
     *
     * Este metodo valida el token JWT, decodifica su contenido y utiliza el nombre de usuario
     * contenido en el token para recuperar los detalles del usuario. Si el token es válido,
     * se crea un objeto de autenticación con los detalles del usuario.
     *
     * @param authentication Objeto de autenticación que contiene las credenciales (el token JWT).
     * @return Un objeto de tipo {@link UsernamePasswordAuthenticationToken} si la autenticación es exitosa.
     * @throws RuntimeException Si el token JWT es inválido o no puede ser decodificado.
     */
    @Override
    public Authentication authenticate(Authentication authentication) {
        // Recupera el token JWT del objeto Authentication
        String token = (String) authentication.getCredentials();

        try {
            // Decodifica el token JWT
            Jwt jwt = jwtDecoder.decode(token);

            // Obtiene el "sub" (subject) del JWT, que generalmente es el nombre de usuario
            String username = jwt.getSubject();

            // Recupera los detalles del usuario utilizando el nombre de usuario
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Si el JWT es válido, crea un token de autenticación con los detalles del usuario
            return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());

        } catch (JwtException e) {
            // Si el token JWT no es válido, lanza una excepción
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    /**
     * Indica si este proveedor de autenticación soporta el tipo de autenticación proporcionado.
     *
     * Este metodo verifica si el tipo de autenticación proporcionado es compatible con este
     * proveedor. En este caso, se soporta la autenticación basada en tokens Bearer.
     *
     * @param authentication Clase del objeto de autenticación.
     * @return {@code true} si el tipo de autenticación es {@link BearerTokenAuthenticationToken}.
     */
    @Override
    public boolean supports(Class<?> authentication) {
        // Soporta la autenticación para BearerTokenAuthenticationToken
        return BearerTokenAuthenticationToken.class.isAssignableFrom(authentication);
    }
}