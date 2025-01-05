package com.tokioschool.filmapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokioschool.filmapp.dto.auth.AuthenticatedMeResponseDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationRequestDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationResponseDTO;
import com.tokioschool.filmapp.services.auth.AuthenticationService;
import com.tokioschool.redis.services.JwtBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@WebMvcTest(controllers = AuthenticationApiController.class) // obtiente solo el contexto del contraldor especificado
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva los filtros de seguridad, si no el "test 1", da "Error de Estado 403"
@ActiveProfiles("test")
class AuthenticationApiControllerWithSecurityDisableUTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean  private AuthenticationService authenticationService; // Mock del servicio
    @MockitoBean  private JwtBlacklistService jwtBlacklistService; // Mock del servicio redis

    @Test
    @Order(1)
    void whenUserLogin_whenPostAuthenticated_whenReturnOk () throws Exception {
        // Configurar los datos de prueba
        AuthenticationRequestDTO requestDTO = AuthenticationRequestDTO.builder()
                .username("testuser")
                .password("password123")
                .build();

        AuthenticationResponseDTO responseDTO = AuthenticationResponseDTO.builder()
                .accessToken("fake-jwt-token")
                .build();

        // Mockear el servicio
        Mockito.when(authenticationService.authenticate(requestDTO)).thenReturn(responseDTO);

        // Realizar la solicitud POST y verificar la respuesta
        mockMvc.perform(MockMvcRequestBuilders.post("/film/api/auth") // Ajusta el endpoint si es necesario
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value("fake-jwt-token")); // Verificar el token en la respuesta
    }


    @Test
    @Order(2)
    void givenUser_whenGetAuthenticatedMe_thenReturnAuthenticatedMe() throws Exception {
        // Simula el DTO de respuesta
        AuthenticatedMeResponseDTO responseDTO = AuthenticatedMeResponseDTO.builder()
                .username("testuser")
                .roles(List.of("ROLE_USER"))
                .build();

        // Mock del servicio
        Mockito.when(authenticationService.getAuthenticated()).thenReturn(responseDTO);

        // Realiza la solicitud con un usuario autenticado
        mockMvc.perform(MockMvcRequestBuilders.get("/film/api/auth/me")
                        //.with(SecurityMockMvcRequestPostProcessors.user("testuser").roles("USER")) // Simula un usuario autenticado
                        //.header("Authorization", "Bearer fake-jwt-token") // Token mock
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()) // Verifica que el estado sea 200
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)) // Verifica el tipo de contenido
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("testuser")) // Verifica el nombre de usuario
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0]").value("ROLE_USER")); // Verifica el rol
    }

    @Test
    @Order(3)
    public void logoutHandler_ValidToken_ShouldReturnOk() throws Exception {
        // Configurar el token y su tiempo de expiración
        String token = "validToken";
        Long expiryTime = System.currentTimeMillis() + 100000L;

        // Simular la respuesta del servicio de autenticación
        Mockito.when(authenticationService.getTokenAndExpiredAt(Mockito.any(HttpServletRequest.class)))
                .thenReturn(Pair.of(token, expiryTime));

        // Ejecutar la solicitud de logout
        mockMvc.perform(MockMvcRequestBuilders.post("/film/api/auth/logout")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verificar que el token se añadió a la lista negra
        Mockito.verify(jwtBlacklistService, Mockito.times(1)).addToBlacklist(token, expiryTime);
    }

    @Test
    @Order(4)
    public void logoutHandler_InvalidToken_ShouldReturnUnauthorized() throws Exception {
        // Simular que el servicio de autenticación devuelve null
        Mockito.when(authenticationService.getTokenAndExpiredAt(Mockito.any(HttpServletRequest.class)))
                .thenReturn(null);

        // Ejecutar la solicitud de logout y esperar una excepción
        mockMvc.perform(MockMvcRequestBuilders.post("/film/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        // Verificar que el servicio de lista negra no se llamó
        Mockito.verify(jwtBlacklistService, Mockito.never()).addToBlacklist(Mockito.anyString(), Mockito.anyLong());
    }
}