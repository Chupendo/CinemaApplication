package com.tokioschool.filmapp.jwt.converter;

import com.tokioschool.jwt.converter.CustomJwtAuthenticationConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("test")
class CustomJwtAuthenticationConverterUTest {

    private final CustomJwtAuthenticationConverter converter = new CustomJwtAuthenticationConverter();

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
        Mockito.when(mockJwt.getClaim("authorities")).thenReturn(null);

        // Act
        AbstractAuthenticationToken authenticationToken = converter.convert(mockJwt);

        // Assert
        Assertions.assertEquals(0, authenticationToken.getAuthorities().size());
    }
}