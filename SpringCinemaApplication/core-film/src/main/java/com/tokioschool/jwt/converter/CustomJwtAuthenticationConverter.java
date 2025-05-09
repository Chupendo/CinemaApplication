package com.tokioschool.jwt.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.List;
import java.util.Optional;

/**
 * Conversor personalizado de autenticación JWT.
 *
 * Esta clase personaliza la conversión de un token JWT en un \{@link AbstractAuthenticationToken\}.
 * Permite extraer y procesar las autoridades (roles y permisos) desde el payload del JWT.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Slf4j
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    /**
     * Clave utilizada para extraer las autoridades del payload del JWT.
     */
    private static final String AUTHORITIES_KEY = "authorities";

    /**
     * Convierte un token JWT en un \{@link AbstractAuthenticationToken\}.
     *
     * Este método personaliza los roles y permisos extrayendo la clave "authorities" del payload
     * del JWT y convirtiéndola en una lista de objetos \{@link GrantedAuthority\}.
     *
     * @param source el token JWT
     * @return el objeto \{@link AbstractAuthenticationToken\} resultante
     */
    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt source) {
        // Conversor predeterminado de autenticación JWT de Spring
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        // Personaliza los roles y permisos si la clave "authorities" está presente
        if (source.getClaim(AUTHORITIES_KEY) != null) {
            jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                    // Extrae la clave "authorities" del payload del JWT y la convierte
                    CustomJwtAuthenticationConverter::getAuthorities
            );
        }
        // Convierte el resto de los campos utilizando el conversor predeterminado
        return jwtAuthenticationConverter.convert(source);
    }

    /**
     * Extrae y convierte las autoridades desde el claim del JWT a una lista de objetos \{@link GrantedAuthority\}.
     *
     * @param jwt el token JWT que contiene el claim de autoridades
     * @return una lista de objetos \{@link GrantedAuthority\}
     * @throws AccessDeniedException si las autoridades son nulas o están vacías
     */
    private static List<GrantedAuthority> getAuthorities(@NonNull Jwt jwt) {
        // Extrae las autoridades como una lista de cadenas
        List<String> authorities = Optional.ofNullable(jwt)
                .map(token -> token.getClaim(AUTHORITIES_KEY))
                .map(auths -> (List<String>) auths)
                .orElseGet(() -> null);

        // Lanza una excepción si las autoridades son nulas o están vacías
        if (authorities == null || authorities.isEmpty()) {
            throw new AccessDeniedException("No authorities");
        }

        // Convierte las cadenas de autoridades en objetos GrantedAuthority
        return authorities.stream()
                .map(authority -> (GrantedAuthority) new SimpleGrantedAuthority(authority))
                .toList();
    }
}