package com.tokioschool.filmapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokioschool.filmapp.dto.auth.AuthenticatedMeResponseDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationRequestDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationResponseDTO;
import com.tokioschool.filmapp.jwt.properties.JwtConfiguration;
import com.tokioschool.filmapp.jwt.properties.JwtConfigurationProperty;
import com.tokioschool.filmapp.security.filter.FilmApiSecurityConfiguration;
import com.tokioschool.filmapp.services.auth.AuthenticationService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@WebMvcTest(controllers = AuthenticationApiController.class) // obtiente solo el contexto del contraldor especificado
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc // Habilita MockMvc con filtros de seguridad
@TestPropertySource(properties = {
        "logging.level.org.springframework.security=DEBUG",
        "application.jwt.secret=secretos123123",
        "application.jwt.expiration=PT1H"
})
@Import({FilmApiSecurityConfiguration.class, JwtConfiguration.class}) // Importa la configuración de seguridad
class AuthenticationApiControllerWithSecurityEnableUTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean  private AuthenticationService authenticationService; // Mock del servicio

    @Test
    void givenUserLogin_whenPostAuthenticated_whenReturnOk () throws Exception {
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
        mockMvc.perform(MockMvcRequestBuilders.post("/film/api/auth") // Ajusta el endpoint si es necesario
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value("fake-jwt-token")); // Verificar el token en la respuesta
    }

    @Test
    @WithMockUser(username = "user",authorities = "ROLE_USER") // Simula un usuario autenticado
    void givenUserAuthenticated_whenGetAuthenticatedMe_thenReturnAuthenticatedMe() throws Exception {
        // Simula el DTO de respuesta
        AuthenticatedMeResponseDTO responseDTO = AuthenticatedMeResponseDTO.builder()
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