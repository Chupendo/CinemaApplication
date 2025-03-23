package com.tokioschool.ratingapp.securities.jwt.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CustomJwtAuthenticationConverterUTest {

    @Mock
    Jwt jwt;

    @Test
    void convert_withValidAuthorities_shouldReturnTokenWithGrantedAuthorities() {
        when(jwt.getClaim("authorities")).thenReturn(List.of("ROLE_USER", "ROLE_ADMIN"));

        CustomJwtAuthenticationConverter converter = new CustomJwtAuthenticationConverter();
        AbstractAuthenticationToken token = converter.convert(jwt);

        List<GrantedAuthority> expectedAuthorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        Assertions.assertNotNull(token);
        Assertions.assertTrue(token.getAuthorities().containsAll(expectedAuthorities));
    }

    @Test
    void convert_withNoAuthorities_shouldReturnTokenWithNoGrantedAuthorities() {
        when(jwt.getClaim("authorities")).thenReturn(List.of("ROLE_USER"));

        CustomJwtAuthenticationConverter converter = new CustomJwtAuthenticationConverter();
        AbstractAuthenticationToken token = converter.convert(jwt);

        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.getAuthorities().isEmpty());
    }

    @Test
    void convert_withNullToken_thenReturnNullPointerException() {


        CustomJwtAuthenticationConverter converter = new CustomJwtAuthenticationConverter();
        Assertions.assertThrows(NullPointerException.class, () ->  converter.convert( null ) );

    }

    @Test
    void convert_withNullAuthorities_thenReturnTokenWithAuthoritiesEmpty() {

        when(jwt.getClaim("authorities")).thenReturn(null);

        CustomJwtAuthenticationConverter converter = new CustomJwtAuthenticationConverter();

        AbstractAuthenticationToken token = converter.convert(jwt);
        Assertions.assertTrue(token.getAuthorities().isEmpty());
    }

    @Test
    void convert_withEmptyAuthorities_thenReturnAccessDeniedException() {

        when(jwt.getClaim("authorities")).thenReturn(List.of());

        CustomJwtAuthenticationConverter converter = new CustomJwtAuthenticationConverter();

        Assertions.assertThrows(AccessDeniedException.class, () ->  converter.convert(jwt) );
    }

    @Test
    void convert_withInvalidAuthoritiesType_thenThrowClassCastException() {
        when(jwt.getClaim("authorities")).thenReturn(Map.of("key", "value"));

        CustomJwtAuthenticationConverter converter = new CustomJwtAuthenticationConverter();

        Assertions.assertThrows(ClassCastException.class, () -> converter.convert(jwt));
    }

}