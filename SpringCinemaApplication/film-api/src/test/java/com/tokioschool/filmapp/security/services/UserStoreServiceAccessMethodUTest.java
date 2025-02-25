package com.tokioschool.filmapp.security.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokioschool.filmapp.controller.UserApiController;
import com.tokioschool.filmapp.dto.user.RoleDTO;
import com.tokioschool.filmapp.dto.user.UserDTO;
import com.tokioschool.filmapp.dto.user.UserFormDTO;
import com.tokioschool.filmapp.jwt.properties.JwtConfiguration;
import com.tokioschool.filmapp.security.filter.FilmApiSecurityConfiguration;
import com.tokioschool.filmapp.services.user.UserService;
import com.tokioschool.filmapp.validation.RegisterUserValidation;
import com.tokioschool.redis.services.JwtBlacklistService;
import com.tokioschool.store.facade.StoreFacade;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
public class UserStoreServiceAccessMethodUTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper; // objeto de serizalizcion, includio en el context de WebMvcTest

    @MockitoBean private StoreFacade storeFacade;
    @MockitoBean private UserService userService;
    @MockitoBean private RegisterUserValidation registerUserValidation;

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
                .roles(List.of("user")).build();

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

        // mock register user validation
        Mockito.when(registerUserValidation.supports(UserFormDTO.class)).thenReturn(true);

        // Prepare UserFormDTO as JSON - Mock UserDTO as JSON part
        MockMultipartFile userPart = new MockMultipartFile(
                "userFormDto",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                userFormDTOJson.getBytes()
        );

        MvcResult mvcResult = this.mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/film/api/users/register")
                                .file(userPart)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        Assertions.assertThat(mvcResult)
                .isNotNull()
                .extracting(MvcResult::getResponse)
                .isNotNull()
                .satisfies(mockHttpServletResponse -> mockHttpServletResponse.getContentAsString().isBlank());

        // opcion 1: Lee la respeusta directamente como un objeto
        //UserFormDTO response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserFormDTO.class);

        // opcion 2: Lee la respeusta como un mapa de valores, cada atributo del objeto de la respuesta es añadio elemento de un mapa
        /*UserFormDTO resutlUserDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<UserFormDTO>() {});

        Assertions.assertThat(resutlUserDto).isNotNull()
                .returns(userFormDTO.getName(),UserFormDTO::getName)
                .returns(userFormDTO.getSurname(),UserFormDTO::getSurname)
                .returns(userFormDTO.getRoles().getFirst(), userFormDTO1 -> userFormDTO1.getRoles().getFirst());
         */
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
                .roles(List.of("user")).build();

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

        // mock register user validation
        Mockito.when(registerUserValidation.supports(UserFormDTO.class)).thenReturn(true);

        // Prepare UserFormDTO as JSON - Mock UserDTO as JSON part
        MockMultipartFile userPart = new MockMultipartFile(
                "userFormDto",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                userFormDTOJson.getBytes()
        );

        MvcResult mvcResult = this.mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/film/api/users/register")
                                .file(userPart)
                ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        Assertions.assertThat(mvcResult.getResponse().getContentAsString()).isNotNull()
                .isInstanceOf(String.class)
                .isBlank();
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
                .roles(List.of("user")).build();

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

        // mock register user validation
        Mockito.when(registerUserValidation.supports(UserFormDTO.class)).thenReturn(true);

        // Prepare UserFormDTO as JSON - Mock UserDTO as JSON part
        MockMultipartFile userPart = new MockMultipartFile(
                "userFormDto",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                userFormDTOJson.getBytes()
        );

        MvcResult mvcResult = this.mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/film/api/users/register")
                                .file(userPart)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        Assertions.assertThat(mvcResult.getResponse().getContentAsString()).isNotNull()
                .satisfies(String::isBlank);
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
                .roles(List.of("user")).build();

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

        // mock register user validation
        Mockito.when(registerUserValidation.supports(UserFormDTO.class)).thenReturn(true);

        // Mock file
        MockMultipartFile file = new MockMultipartFile(
                "file",                      // Part name
                "test-file.txt",             // Original file name
                MediaType.TEXT_PLAIN_VALUE,  // Content type
                "Sample file content".getBytes() // File content
        );

        // Prepare UserFormDTO as JSON - Mock UserDTO as JSON part
        MockMultipartFile userPart = new MockMultipartFile(
                "userFormDto",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                userFormDTOJson.getBytes()
        );

        MvcResult mvcResult = this.mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/film/api/users/update/{userId}",userFormDTO.getId())
                                .file(file)
                                .file(userPart)
                                .with(request -> {
                                    request.setMethod("PUT"); // Override to PUT since multipart defaults to POST
                                    return request;
                                }))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        // opcion 1: Lee la respeusta directamente como un objeto
        UserFormDTO resultUserFormDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserFormDTO.class);

        Assertions.assertThat(resultUserFormDTO).isNotNull()
                .returns(userFormDTO.getName(),UserFormDTO::getName)
                .returns(userFormDTO.getSurname(),UserFormDTO::getSurname)
                .returns(userFormDTO.getRoles().getFirst(), userFormDTO1 -> userFormDTO1.getRoles().getFirst());
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
                .roles(List.of("user")).build();

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
