package com.tokioschool.storeapp.controller.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokioschool.storeapp.controller.AuthenticationApiController;
import com.tokioschool.storeapp.dto.authentication.AuthenticatedMeResponseDTO;
import com.tokioschool.storeapp.dto.authentication.AuthenticationRequestDTO;
import com.tokioschool.storeapp.dto.authentication.AuthenticationResponseDTO;
import com.tokioschool.storeapp.security.filter.StoreApiSecurityConfiguration;
import com.tokioschool.storeapp.security.jwt.configuration.JwtConfiguration;
import com.tokioschool.storeapp.service.AuthenticationService;
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

@WebMvcTest(controllers = AuthenticationApiController.class) // obtiente solo el contexto del contraldor especificado
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({StoreApiSecurityConfiguration.class, JwtConfiguration.class}) // Importa la configuración de seguridad
class AuthenticationApiControllerITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    void givenAuthUserDto_whenPostAuthenticated_returnOk() throws Exception {

        AuthenticationRequestDTO authenticationRequestDTO = AuthenticationRequestDTO.builder()
                .username("consumer")
                .password("password").build();

        AuthenticationResponseDTO authenticationResponseDTO = AuthenticationResponseDTO.builder()
                .accessToken("token")
                .expiresIn(1L).build();

        Mockito.when(authenticationService.authenticate(authenticationRequestDTO))
                        .thenReturn(authenticationResponseDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/store/api/auth") // Ajusta el endpoint si es necesario
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(authenticationRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithAnonymousUser
    void givenAnonymousUser_whenGetAuthenticatedMe_thenReturnAccessDined() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/store/api/auth/me"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }


    @Test
    @WithMockUser(username = "john",roles = "USER")
    void givenUserAuthenticated_whenGetAuthenticatedMe_thenReturnAccessDined() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/store/api/auth/me"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "john",roles = "ADMIN")
    void givenAdminAuthenticated_whenGetAuthenticatedMe_thenReturnOk() throws Exception {

        Mockito.when(authenticationService.getAuthenticated())
                        .thenReturn(AuthenticatedMeResponseDTO
                                .builder()
                                .username("john")
                                .authorities(List.of("read","writer"))
                                .roles(List.of("admin","user"))
                                .build());

        mockMvc.perform(MockMvcRequestBuilders.get("/store/api/auth/me"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}