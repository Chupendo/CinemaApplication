package com.tokioschool.storeapp.service.impl.it;

import com.tokioschool.storeapp.dto.authentication.AuthenticatedMeResponseDTO;
import com.tokioschool.storeapp.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class PreAuthorizeAuthenticationServiceImplITest {

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @WithMockUser(username = "adminUser", roles = {"ADMIN"})
    public void givenUserLogin_whenGetAuthenticatedWithAdminRole_returnOk() throws Exception {
        AuthenticatedMeResponseDTO response = authenticationService.getAuthenticated();
        Assertions.assertNotNull(response);
        Assertions.assertEquals("adminUser", response.getUsername());
    }

    @Test
    @WithMockUser(username = "regularUser", roles = {"USER"})
    public void givenUserLogin_whenGetAuthenticatedWithUserRole_returnAuthorizationDeniedException() {
        Assertions.assertThrows(AuthorizationDeniedException.class, () -> {
            authenticationService.getAuthenticated();
        });
    }

    @Test
    public void givenUserNotLogin_whenGetAuthenticated_returnAuthenticationCredentialsNotFoundException() {
        Assertions.assertThrows(AuthenticationCredentialsNotFoundException.class, () -> {
            authenticationService.getAuthenticated();
        });
    }

    @Test
    @WithAnonymousUser
    public void givenUWithAnonymousUser_whenGetAuthenticated_returnAuthorizationDeniedException() {
        Assertions.assertThrows(AuthorizationDeniedException.class, () -> {
            authenticationService.getAuthenticated();
        });
    }

    @Test
    //@WithMockUser(username = "test-user", roles = {"USER"}) // se reemplaza por SecurityContextHolder.setContext(securityContext);
    void givenAuthenticatedUser_whenGetTokenAndExpiredAt_thenReturnValidToken() {
        // Simular un JWT con un valor de token y fecha de expiración
        Instant expirationTime = Instant.now().plusSeconds(3600);
        Jwt jwt = Jwt.withTokenValue("mocked-jwt-token")
                .header("test-user", "HS256")
                .claim("sub", "test-user")
                .claim("authorities", List.of("ROLE_USER"))
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        // Crear el token de autenticación con el JWT mockeado
        JwtAuthenticationToken jwtAuthToken = new JwtAuthenticationToken(jwt, Collections.emptyList());

        // Mock del contexto de seguridad para establecer el usuario autenticado
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(jwtAuthToken);
        SecurityContextHolder.setContext(securityContext);

        // Mock de la petición HTTP con el token en la cabecera
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer mocked-jwt-token");

        // Llamar al metodo que se está probando
        var result = authenticationService.getTokenAndExpiredAt(request);

        // Verificar que el resultado es correcto
        Assertions.assertNotNull(result, "El resultado no debe ser nulo");
        Assertions. assertEquals("mocked-jwt-token", result.getLeft(), "El token debe coincidir");
        Assertions.assertEquals(expirationTime.getEpochSecond(), result.getRight(), "La fecha de expiración debe coincidir");
    }

    @Test
    //@WithMockUser(username = "test-user", roles = {"USER"}) // se reemplaza por SecurityContextHolder.setContext(securityContext);
    void givenInvalidToken_whenGetTokenAndExpiredAt_thenReturnNull() {
        // Simular un JWT con valores incorrectos
        Jwt jwt = Jwt.withTokenValue("invalid-jwt-token")
                .header("test-user", "HS256")
                .expiresAt(Instant.now().plusSeconds(3600))
                .issuedAt(Instant.now())
                .subject("test-user")
                .build();

        JwtAuthenticationToken jwtAuthToken = new JwtAuthenticationToken(jwt,Collections.emptyList());

        // Mock del contexto de seguridad con JWT inválido
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(jwtAuthToken);
        SecurityContextHolder.setContext(securityContext);

        // Mock de la petición HTTP con un token diferente en la cabecera
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer another-jwt-token");

        // Llamar al metodo de prueba
        var result = authenticationService.getTokenAndExpiredAt(request);

        // Verificar que devuelve null debido a la falta de coincidencia del token
        Assertions.assertNull(result, "El resultado debe ser nulo si los tokens no coinciden");
    }


    @Test
    void givenNoAuthentication_whenGetTokenAndExpiredAt_thenReturnAuthenticationCredentialsNotFoundException() {
        // Limpiar el contexto de seguridad (sin usuario autenticado)
        SecurityContextHolder.clearContext();

        // Mock de la petición HTTP con token en la cabecera
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        Assertions.assertThrows(AuthenticationCredentialsNotFoundException.class, () -> {
            authenticationService.getTokenAndExpiredAt(request);
        });
    }
}
