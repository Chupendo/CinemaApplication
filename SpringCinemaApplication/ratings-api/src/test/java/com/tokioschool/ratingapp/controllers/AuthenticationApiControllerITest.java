package com.tokioschool.ratingapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokioschool.filmapp.dto.auth.AuthenticatedMeResponseDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationRequestDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationResponseDTO;
import com.tokioschool.ratingapp.securities.configurations.filter.RatingsApiSecurityConfiguration;
import com.tokioschool.ratingapp.securities.jwt.configuration.JwtConfiguration;
import com.tokioschool.ratingapp.services.AuthenticationService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = AuthenticationApiController.class) // obtiente solo el contexto del contraldor especificado
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc // Habilita MockMvc con filtros de seguridad
/*
@ContextConfiguration(classes = {
        AuthenticationApiController.class,
        RatingsApiSecurityConfiguration.class,
        JwtConfiguration.class
})*/
@ActiveProfiles("test")
@Import({RatingsApiSecurityConfiguration.class,
        JwtConfiguration.class})
class AuthenticationApiControllerITest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private AuthenticationService authenticationService;


    @Test
    @WithAnonymousUser
    public void anonymousUser_whenRequestAuth_thenReturnToken() throws Exception {

        // Configurar los datos de prueba
        AuthenticationRequestDTO requestDTO = AuthenticationRequestDTO.builder()
                .username("user")
                .password("user")
                .build();

        AuthenticationResponseDTO responseDTO = AuthenticationResponseDTO.builder()
                .accessToken("fake-jwt-token")
                .build();

        // Mockear el servicio
        Mockito.when(authenticationService.authenticate(requestDTO)).thenReturn(responseDTO);

        // Realizar la solicitud POST y verificar la respuesta
        mockMvc.perform(post("/ratings/api/auth")
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString(requestDTO)) )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value("fake-jwt-token"));
    }

    @Test
    public void noUser_whenRequestAuth_thenReturnToken() throws Exception {
        // Configurar los datos de prueba
        AuthenticationRequestDTO requestDTO = AuthenticationRequestDTO.builder()
                .username("user")
                .password("user")
                .build();

        // Realizar la solicitud POST y verificar la respuesta
        mockMvc.perform(post("/ratings/api/auth")
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString(requestDTO)) )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "user",roles = "user")
    public void userAuth_whenRequestAuth_thenReturnForbidden() throws Exception {
        // Configurar los datos de prueba
        AuthenticationRequestDTO requestDTO = AuthenticationRequestDTO.builder()
                .username("user")
                .password("user")
                .build();

        // Realizar la solicitud POST y verificar la respuesta
        mockMvc.perform(post("/ratings/api/auth")
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString(requestDTO)) )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user",roles = "USER")
    public void user_whenAuthMe_thenReturnForbidden() throws Exception {

        // Realizar la solicitud POST y verificar la respuesta
        mockMvc.perform(post("/ratings/api/auth/me")
                        .contentType( MediaType.APPLICATION_JSON )
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN") // Simula un usuario autenticado
    void admin_whenAuthMe_thenReturnUserData() throws Exception {
        // Simula el DTO de respuesta
        AuthenticatedMeResponseDTO responseDTO = AuthenticatedMeResponseDTO.builder()
                .username("admin")
                .roles(List.of("ROLE_ADMIN"))
                .build();

        // Mock del servicio
        Mockito.when(authenticationService.getAuthenticated()).thenReturn(responseDTO);

        // Realiza la solicitud con un usuario autenticado
        mockMvc.perform(MockMvcRequestBuilders.get("/ratings/api/auth/me")
                        //.with(SecurityMockMvcRequestPostProcessors.user("user").roles("USER")) // Simula un usuario autenticado
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()) // Verifica que el estado sea 200
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)) // Verifica el tipo de contenido
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("admin")) // Verifica el nombre de usuario
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0]").value("ROLE_ADMIN")); // Verifica el rol
    }

}