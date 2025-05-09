package com.tokioschool.security.jwt.service.impl.ut;

import com.tokioschool.storeapp.security.jwt.properties.JwtConfigurationProperty;
import com.tokioschool.storeapp.security.jwt.service.impl.JwtServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class JwtServiceImplUTest {

    @Mock
    private JwtConfigurationProperty jwtConfigurationProperty;

    @Mock
    private NimbusJwtEncoder nimbusJwtEncoder;

    @InjectMocks
    private JwtServiceImpl jwtService;

    @Test
    void givenUser_whenGenerateToken_returnTokenJwt() {
        // Arrange
        Mockito.when(jwtConfigurationProperty.expiration()).thenReturn(Duration.ofMinutes(30));
        Jwt mockJwt = Mockito.mock(Jwt.class);
        Mockito.when(nimbusJwtEncoder.encode(Mockito.any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        User userDetails = new User("test-user", "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        // Act
        Jwt token = jwtService.generateToken(userDetails);

        // Assert
        Assertions.assertNotNull(token, "El token generado no debería ser null");
        Mockito.verify(jwtConfigurationProperty).expiration();
        Mockito.verify(nimbusJwtEncoder).encode(Mockito.any(JwtEncoderParameters.class));
    }

}