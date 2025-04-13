package com.tokioschool.storeapp.security.jwt.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.List;

/**
 * Convertidor personalizado para tokens JWT en Spring Security.
 *
 * Esta clase implementa la interfaz `Converter` para convertir un objeto `Jwt` en una
 * instancia de `AbstractAuthenticationToken`. Personaliza la conversión de los roles
 * y permisos definidos en el token JWT.
 *
 * Notas:
 * - Utiliza el campo `authorities` del payload del token JWT para mapear los roles y permisos.
 * - Convierte los valores de `authorities` en objetos de tipo `SimpleGrantedAuthority`.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    /**
     * Convierte un token JWT en un objeto de autenticación.
     *
     * Este metodo personaliza la conversión de los roles y permisos definidos en el token JWT,
     * utilizando el campo `authorities` del payload. Los valores de `authorities` se convierten
     * en instancias de `SimpleGrantedAuthority` para adaptarse al modelo de Spring Security.
     *
     * @param source El token JWT que se va a convertir.
     * @return Una instancia de `AbstractAuthenticationToken` con los datos del token JWT.
     */
    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        // Convertidor por defecto de Spring Security
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        // Personalización de los roles y permisos
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                // Obtiene el campo "authorities" del payload del token (un String[])
                // y lo convierte en objetos de tipo "SimpleGrantedAuthority"
                jwt -> ((List<String>) jwt.getClaim("authorities"))
                        .stream().map(authority -> (GrantedAuthority) new SimpleGrantedAuthority(authority))
                        .toList()
        );

        // Convierte el resto de los campos utilizando el convertidor por defecto
        return jwtAuthenticationConverter.convert(source);
    }
}