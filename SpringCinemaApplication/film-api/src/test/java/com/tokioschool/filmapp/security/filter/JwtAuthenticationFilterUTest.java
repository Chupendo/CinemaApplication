package com.tokioschool.filmapp.security.filter;

import com.tokioschool.redis.services.JwtBlacklistService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
class JwtAuthenticationFilterUTest {

    @Mock
    private JwtBlacklistService jwtBlacklistService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
    }

    // prueba de token en la lista negra
    @Test
    public void doFilterInternal_TokenInBlacklist_ShouldReturnUnauthorized() throws Exception {
        // Configurar el token y la cabecera Authorization
        String token = "blacklistedToken";
        request.addHeader("Authorization", "Bearer " + token);

        // Simular que el token está en la lista negra
        Mockito.when(jwtBlacklistService.isBlacklisted(token)).thenReturn(true);

        // Ejecutar el filtro
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verificar que la respuesta es 401 Unauthorized
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        Mockito.verify(jwtBlacklistService, Mockito.times(1)).isBlacklisted(token);
    }

    // prueba cuando no hay token en la request
    @Test
    public void doFilterInternal_NoToken_ShouldContinueFilterChain() throws Exception {
        // No agregar cabecera Authorization

        // Ejecutar el filtro
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verificar que la respuesta es 200 OK y que el filtro permitió continuar la cadena
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
        Mockito.verify(jwtBlacklistService, Mockito.never()).isBlacklisted(Mockito.anyString());
    }

    // prueba cuando el token no esta en la lista
    @Test
    public void doFilterInternal_ValidToken_ShouldContinueFilterChain() throws Exception {
        // Configurar el token y la cabecera Authorization
        String token = "validToken";
        request.addHeader("Authorization", "Bearer " + token);

        // Simular que el token no está en la lista negra
        Mockito.when(jwtBlacklistService.isBlacklisted(token)).thenReturn(false);

        // Ejecutar el filtro
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verificar que la respuesta es 200 OK y que el filtro permitió continuar la cadena
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
        Mockito.verify(jwtBlacklistService, Mockito.times(1)).isBlacklisted(token);
    }
}