package com.tokioschool.filmapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokioschool.filmapp.dto.auth.AuthenticatedMeResponseDto;
import com.tokioschool.filmapp.dto.auth.AuthenticationRequestDto;
import com.tokioschool.filmapp.dto.auth.AuthenticationResponseDto;
import com.tokioschool.filmapp.security.confings.JwtConfiguration;
import com.tokioschool.filmapp.security.filter.FilmApiSecurityConfiguration;
import com.tokioschool.filmapp.services.auth.AuthenticationService;
import com.tokioschool.filmapp.services.user.UserService;
import com.tokioschool.redis.services.JwtBlacklistService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "logging.level.org.springframework.security=DEBUG",
        "jwt.secret=secretos123123",
        "jwt.expiration=PT1H",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.liquibase.enabled=false"
})
class AuthenticationApiControllerWithSecurityEnableUTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean  private AuthenticationService authenticationService; // Mock del servicio
    @MockitoBean  private JwtBlacklistService jwtBlacklistService; // Mock del servicio redis

    // required for log filter
    @MockitoBean private UserService userService;

    @Test
    void givenUserLogin_whenPostAuthenticated_whenReturnOk () throws Exception {
        // Configurar los datos de prueba
        AuthenticationRequestDto requestDTO = AuthenticationRequestDto.builder()
                .username("user")
                .password("user")
                .build();

        AuthenticationResponseDto responseDTO = AuthenticationResponseDto.builder()
                .accessToken("fake-jwt-secret")
                .build();

        // Mockear el servicio
        Mockito.when(authenticationService.authenticate(requestDTO)).thenReturn(responseDTO);

        // Realizar la solicitud POST y verificar la respuesta
        mockMvc.perform(MockMvcRequestBuilders.post("/film/api/auth") // Ajusta el endpoint si es necesario
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token").value("fake-jwt-secret")); // Verificar el secret en la respuesta
    }

    @Test
    @WithMockUser(username = "user",authorities = "ROLE_USER") // Simula un usuario autenticado
    void givenUserAuthenticated_whenGetAuthenticatedMe_thenReturnAuthenticatedMe() throws Exception {
        // Simula el DTO de respuesta
        AuthenticatedMeResponseDto responseDTO = AuthenticatedMeResponseDto.builder()
                .username("user")
                .roles(List.of("ROLE_USER"))
                .build();

        // Mock del servicio
        Mockito.when(authenticationService.getAuthenticated()).thenReturn(responseDTO);

        // Realiza la solicitud con un usuario autenticado
        mockMvc.perform(MockMvcRequestBuilders.get("/film/api/auth/me")
                        //.with(SecurityMockMvcRequestPostProcessors.user("user").roles("USER")) // Simula un usuario autenticado
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()) // Verifica que el estado sea 200
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)) // Verifica el tipo de contenido
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("user")) // Verifica el nombre de usuario
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0]").value("ROLE_USER")); // Verifica el rol
    }
}