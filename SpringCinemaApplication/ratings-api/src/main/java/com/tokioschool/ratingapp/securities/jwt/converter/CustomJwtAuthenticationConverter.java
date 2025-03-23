package com.tokioschool.ratingapp.securities.jwt.converter;


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
import java.util.Objects;
import java.util.Optional;


/**
 * Custom JWT Authentication Converter.
 * This class customizes the conversion of a JWT token to an AbstractAuthenticationToken.
 */
@Slf4j
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    /**
     * Converts a JWT token to an AbstractAuthenticationToken.
     * This method customizes the roles and permissions by extracting the "authorities" claim
     * from the JWT payload and converting it to a list of GrantedAuthority objects.
     *
     * @param source the JWT token
     * @return the AbstractAuthenticationToken
     */
    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt source) {
        // Default Spring JWT Authentication Converter
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        // Customize roles and permissions
        if(source.getClaim("authorities") != null) {
            jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                    // Extract the "authorities" claim from the JWT payload, which is a String[],
                    // and convert it to a list of SimpleGrantedAuthority objects
                    CustomJwtAuthenticationConverter::getAuthorities
            );
        }
        // Convert the rest of the fields using the default converter
        return jwtAuthenticationConverter.convert(source);
    }

    /**
     * Extracts and converts the authorities from the JWT claim to a list of GrantedAuthority objects.
     *
     * @param jwt the JWT token containing the authorities claim
     * @return a list of GrantedAuthority objects
     * @throws AccessDeniedException if the authorities are null or empty
     */
    private static List<GrantedAuthority> getAuthorities(@NonNull Jwt jwt) {
        List<String> authorities = Optional.ofNullable(jwt)
                .map(token -> token.getClaim("authorities") )
                .map(auths -> (List<String>) auths)
                .orElseGet(() -> null);

        if (authorities == null || authorities.isEmpty()) {
            throw new AccessDeniedException("No authorities");
        }

        return authorities.stream()
                .map(authority -> (GrantedAuthority) new SimpleGrantedAuthority(authority))
                .toList();
    }
}
