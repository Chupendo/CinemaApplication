package com.tokioschool.filmapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.jwt.properties.JwtConfiguration;
import com.tokioschool.filmapp.security.filter.FilmApiSecurityConfiguration;
import com.tokioschool.filmapp.services.artist.ArtistService;
import com.tokioschool.filmapp.services.user.UserService;
import com.tokioschool.redis.services.JwtBlacklistService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@WebMvcTest(controllers = ArtistApiController.class) // obtiente solo el contexto del contraldor especificado
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc // Habilita MockMvc con filtros de seguridad
@TestPropertySource(properties = {
        "logging.level.org.springframework.security=DEBUG",
        "application.jwt.secret=secretos123123",
        "application.jwt.expiration=PT1H"
})
@Import({FilmApiSecurityConfiguration.class, JwtConfiguration.class}) // Importa la configuración de seguridad
class ArtistApiControllerWithSecurityEnableUTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ArtistService artistService;

    // required for log filter
    @MockitoBean    private UserService userService;
    @MockitoBean    private JwtBlacklistService jwtBlacklistService;

    @Test
    @Order(1)
    @WithMockUser(username = "user",roles = "user")
    void givenAuthenticated_whenGetFindAll_whenReturnOk() throws Exception {

        // Mockear el servicio
        Mockito.when(artistService.findByAll()).thenReturn(List.of(ArtistDto.builder().build()));

        // Realizar la solicitud POST y verificar la respuesta
        mockMvc.perform(MockMvcRequestBuilders.get("/film/api/artists/find-all") // Ajusta el endpoint si es necesario
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @Order(2)
    void givenNoAuthenticated_whenGetFindAll_whenReturnUnauthorized() throws Exception {

        // Mockear el servicio
        Mockito.when(artistService.findByAll()).thenReturn(List.of(ArtistDto.builder().build()));

        // Realizar la solicitud POST y verificar la respuesta
        mockMvc.perform(MockMvcRequestBuilders.get("/film/api/artists/find-all") // Ajusta el endpoint si es necesario
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @Order(3)
    @WithAnonymousUser
    void givenAuthenticatedAnonymousUser_whenGetFindAll_whenReturnUnauthorized() throws Exception {

        // Mockear el servicio
        Mockito.when(artistService.findByAll()).thenReturn(List.of(ArtistDto.builder().build()));

        // Realizar la solicitud POST y verificar la respuesta
        mockMvc.perform(MockMvcRequestBuilders.get("/film/api/artists/find-all") // Ajusta el endpoint si es necesario
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @Order(4)
    @WithMockUser(username = "user",roles = "user")
    void givenAuthenticated_whenSave_whenReturnOk() throws Exception {

        ArtistDto artistDto = ArtistDto.builder().name("abc").surname("surn").typeArtist("ACTOR").build();
        String artistDtoJson = objectMapper.writeValueAsString(artistDto);

        // Mockear el servicio
        Mockito.when(artistService.registerArtist(artistDto)).thenReturn(artistDto);


        // Realizar la solicitud POST y verificar la respuesta
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/film/api/artists") // Ajusta el endpoint si es necesario
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(artistDtoJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ArtistDto resultArtistDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),ArtistDto.class);

        Assertions.assertThat(resultArtistDto)
                .returns(artistDto.getName(),ArtistDto::getName)
                .returns(artistDto.getSurname(),ArtistDto::getSurname)
                .returns(artistDto.getTypeArtist(),ArtistDto::getTypeArtist);
    }
}