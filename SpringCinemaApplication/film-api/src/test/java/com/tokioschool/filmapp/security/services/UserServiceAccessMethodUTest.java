package com.tokioschool.filmapp.security.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokioschool.filmapp.controller.UserApiController;
import com.tokioschool.filmapp.dto.user.RoleDTO;
import com.tokioschool.filmapp.dto.user.UserDTO;
import com.tokioschool.filmapp.dto.user.UserFormDTO;
import com.tokioschool.filmapp.jwt.properties.JwtConfiguration;
import com.tokioschool.filmapp.security.filter.FilmApiSecurityConfiguration;
import com.tokioschool.filmapp.services.user.UserService;
import com.tokioschool.redis.services.JwtBlacklistService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;

@WebMvcTest(controllers = UserApiController.class) // obtiente solo el contexto del contraldor especificado
@ActiveProfiles("test")
@AutoConfigureMockMvc // Habilita MockMvc con filtros de seguridad
@TestPropertySource(properties = {
        "logging.level.org.springframework.security=DEBUG",
        "application.jwt.secret=secretos123123",
        "application.jwt.expiration=PT1H"
})
@Import({FilmApiSecurityConfiguration.class, JwtConfiguration.class,}) // Importa la configuración de seguridad
public class UserServiceAccessMethodUTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper; // objeto de serizalizcion, includio en el context de WebMvcTest

    @MockitoBean private UserService userService;
    @MockitoBean private JwtBlacklistService jwtBlacklistService; // Mock del servicio redis

    @Test
    @WithMockUser(username = "ADMIN",roles = "ADMIN")
    void givenAdmin_whenRegister_thenReturnOk() throws Exception {
        UserFormDTO userFormDTO = UserFormDTO.builder()
                .name("John")
                .surname("Doe")
                .password("Contraseña1@")
                .passwordBis("Contraseña1@")
                .username("johndoe")
                .email("johndoe@example.com")
                .birthDate(LocalDate.now().minusYears(30))
                .role("user").build();

        UserDTO userDTO = UserDTO.builder()
                .id("00001ABC")
                .name("John")
                .surname("Doe")
                .username("johndoe")
                .email("johndoe@example.com")
                .birthDate(LocalDate.now().minusYears(30))
                .roles(List.of(RoleDTO.builder().id(1L).name("user").authorities(List.of("read")).build()))
                .build();

        // Convert UserFormDTO to JSON
        String userFormDTOJson = objectMapper.writeValueAsString(userFormDTO);

        Mockito.when(userService.registerUser(Mockito.any(UserFormDTO.class)))
                .thenReturn(userDTO);

        MvcResult mvcResult = this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/film/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(userFormDTOJson)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // opcion 1: Lee la respeusta directamente como un objeto
        //UserDTO response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDTO.class);

        // opcion 2: Lee la respeusta como un mapa de valores, cada atributo del objeto de la respuesta es añadio elemento de un mapa
        UserDTO resutlUserDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<UserDTO>() {});

        Assertions.assertThat(resutlUserDto).isNotNull()
                .returns(userFormDTO.getName(),UserDTO::getName)
                .returns(userFormDTO.getSurname(),UserDTO::getSurname)
                .returns(userFormDTO.getRole(),userDTO1 -> userDTO1.getRoles().getFirst().getName());
    }

    @Test
    @WithMockUser(username = "USER",roles = "USER")
    void givenUser_whenRegister_thenReturnOk() throws Exception {
        UserFormDTO userFormDTO = UserFormDTO.builder()
                .name("John")
                .surname("Doe")
                .password("Contraseña1@")
                .passwordBis("Contraseña1@")
                .username("johndoe")
                .email("johndoe@example.com")
                .birthDate(LocalDate.now().minusYears(30))
                .role("user").build();

        UserDTO userDTO = UserDTO.builder()
                .id("00001ABC")
                .name("John")
                .surname("Doe")
                .username("johndoe")
                .email("johndoe@example.com")
                .birthDate(LocalDate.now().minusYears(30))
                .roles(List.of(RoleDTO.builder().id(1L).name("user").authorities(List.of("read")).build()))
                .build();

        // Convert UserFormDTO to JSON
        String userFormDTOJson = objectMapper.writeValueAsString(userFormDTO);
        Mockito.when(userService.registerUser(Mockito.any(UserFormDTO.class)))
                .thenReturn(userDTO);


        MvcResult mvcResult = this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/film/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(userFormDTOJson)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // opcion 1: Lee la respeusta directamente como un objeto
        UserDTO resutlUserDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<UserDTO>() {});

        Assertions.assertThat(resutlUserDto).isNotNull()
                .returns(userFormDTO.getName(),UserDTO::getName)
                .returns(userFormDTO.getSurname(),UserDTO::getSurname)
                .returns(userFormDTO.getRole(),userDTO1 -> userDTO1.getRoles().getFirst().getName());
    }

    @Test
    @WithAnonymousUser
    void givenUserAnonymous_whenRegister_thenReturnOk() throws Exception {
        UserFormDTO userFormDTO = UserFormDTO.builder()
                .name("John")
                .surname("Doe")
                .password("Contraseña1@")
                .passwordBis("Contraseña1@")
                .username("johndoe")
                .email("johndoe@example.com")
                .birthDate(LocalDate.now().minusYears(30))
                .role("user").build();

        UserDTO userDTO = UserDTO.builder()
                .id("00001ABC")
                .name("John")
                .surname("Doe")
                .username("johndoe")
                .email("johndoe@example.com")
                .birthDate(LocalDate.now().minusYears(30))
                .roles(List.of(RoleDTO.builder().id(1L).name("user").authorities(List.of("read")).build()))
                .build();

        // Convert UserFormDTO to JSON
        String userFormDTOJson = objectMapper.writeValueAsString(userFormDTO);
        Mockito.when(userService.registerUser(Mockito.any(UserFormDTO.class)))
                .thenReturn(userDTO);


        MvcResult mvcResult = this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/film/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(userFormDTOJson)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // opcion 1: Lee la respeusta directamente como un objeto
        UserDTO resutlUserDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<UserDTO>() {});

        Assertions.assertThat(resutlUserDto).isNotNull()
                .returns(userFormDTO.getName(),UserDTO::getName)
                .returns(userFormDTO.getSurname(),UserDTO::getSurname)
                .returns(userFormDTO.getRole(),userDTO1 -> userDTO1.getRoles().getFirst().getName());
    }

    @Test
    @WithMockUser(username = "USER",roles = "USER")
    void givenUserAuthenticated_whenUpdateUser_thenReturnOk() throws Exception {

        UserFormDTO userFormDTO = UserFormDTO.builder()
                .id("00001ABC")
                .name("John")
                .surname("Doe")
                .password("Contraseña1@")
                .passwordBis("Contraseña1@")
                .username("johndoe")
                .email("johndoe@example.com")
                .birthDate(LocalDate.now().minusYears(30))
                .role("user").build();

        UserDTO userDTO = UserDTO.builder()
                .id("00001ABC")
                .name("John")
                .surname("Doe")
                .username("johndoe")
                .email("johndoe@example.com")
                .birthDate(LocalDate.now().minusYears(30))
                .roles(List.of(RoleDTO.builder().id(1L).name("user").authorities(List.of("read")).build()))
                .build();

        // Convert UserFormDTO to JSON
        String userFormDTOJson = objectMapper.writeValueAsString(userFormDTO);

        Mockito.when(userService.updateUser(userFormDTO.getId(),userFormDTO))
                .thenReturn(userDTO);

        MvcResult mvcResult = this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/film/api/users/update/{userId}",userFormDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(userFormDTOJson)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // opcion 1: Lee la respeusta directamente como un objeto
        UserDTO resutlUserDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDTO.class);

        Assertions.assertThat(resutlUserDto).isNotNull()
                .returns(userFormDTO.getName(),UserDTO::getName)
                .returns(userFormDTO.getSurname(),UserDTO::getSurname)
                .returns(userFormDTO.getRole(),userDTO1 -> userDTO1.getRoles().getFirst().getName());
    }

    @Test
    @WithAnonymousUser
    void givenUserNoAuthenticated_whenUpdateUser_thenError() throws Exception {
        UserFormDTO userFormDTO = UserFormDTO.builder()
                .id("00001ABC")
                .name("John")
                .surname("Doe")
                .password("Contraseña1@")
                .passwordBis("Contraseña1@")
                .username("johndoe")
                .email("johndoe@example.com")
                .birthDate(LocalDate.now().minusYears(30))
                .role("user").build();

        // Convert UserFormDTO to JSON
        String userFormDTOJson = objectMapper.writeValueAsString(userFormDTO);


        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/film/api/users/update/{userId}",userFormDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(userFormDTOJson)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andReturn();
    }

    @TestConfiguration
    @EnableMethodSecurity
    public static class Init {
    }
}
