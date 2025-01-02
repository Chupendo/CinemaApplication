package com.tokioschool.filmapp.jwt.converter;


import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.List;

/**
 * This need for mapper the roles and privileges of user to authenticate,
 * and used the Convert of Spring
 *
 * @author andres.rpenuel.a
 * @version 1.0
 */
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String AUTHORITIES_KEY = "authorities";

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        // Convert por defecto de Spring
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        // personalizamos los roles y permisos
        if( source.getClaims()!= null && source.getClaim(AUTHORITIES_KEY) != null ) {
            jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                    // se obtiene el campo de "authorities" del payload del token que es un String[]
                    // y se convierte a un objeto de tipo "SimpleGrantedAuthoritiy" para adaptarlo
                    // a UserDetails
                    jwt -> ((List<String>) jwt.getClaim(AUTHORITIES_KEY))
                            .stream().map(authority -> (GrantedAuthority) new SimpleGrantedAuthority(authority))
                            .toList()
            );
        }
        // se convierte el resto de campos por defecto
        return jwtAuthenticationConverter.convert(source);
    }
}
