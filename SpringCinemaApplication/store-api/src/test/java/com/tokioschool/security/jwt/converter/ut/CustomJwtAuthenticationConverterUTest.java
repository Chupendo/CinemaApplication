package com.tokioschool.security.jwt.converter.ut;

import com.tokioschool.storeapp.security.jwt.converter.CustomJwtAuthenticationConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("test")
class CustomJwtAuthenticationConverterUTest {


    private static CustomJwtAuthenticationConverter converter;

    @BeforeAll
    static void setUp(){
        converter = new CustomJwtAuthenticationConverter();
    }

    @Test
    void givenAJwtWithAuthorities_whenConvert_thenReturnAuthenticationToken() {
        // Arrange
        Jwt mockJwt = Mockito.mock(Jwt.class);

        // Simular el payload del JWT con el claim "authorities"
        Mockito.when(mockJwt.getClaim("authorities")).thenReturn(List.of("ROLE_ADMIN", "ROLE_USER"));

        // Act
        AbstractAuthenticationToken authenticationToken = converter.convert(mockJwt);

        // Assert
        Assertions.assertEquals(2, authenticationToken.getAuthorities().size());
        Assertions.assertEquals(
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER")),
                authenticationToken.getAuthorities().stream().toList()
        );
    }

    @Test
    void givenAJwtWithOutAuthorities_whenConvert_thenReturnAuthenticationTokenEmpty() {
        // Arrange
        Jwt mockJwt = Mockito.mock(Jwt.class);

        // Simular el payload del JWT con el claim "authorities"
        Mockito.when(mockJwt.getClaim("authorities")).thenReturn(List.of());

        // Act
        AbstractAuthenticationToken authenticationToken = converter.convert(mockJwt);

        // Assert
        Assertions.assertEquals(0, authenticationToken.getAuthorities().size());
    }

    @Test
    void givenAJwtWithAuthoritiesNull_whenConvert_thenNullPointerException() {
        // Arrange
        Jwt mockJwt = Mockito.mock(Jwt.class);

        // Simular el payload del JWT con el claim "authorities"
        Mockito.when(mockJwt.getClaim("authorities")).thenReturn(null);


        Assertions.assertThrows(NullPointerException.class, ()->  converter.convert(mockJwt));
    }
}