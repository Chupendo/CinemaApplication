package com.tokioschool.filmapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokioschool.filmapp.security.confings.JwtAuthenticationProvider;
import com.tokioschool.filmapp.security.confings.JwtConfiguration;
import com.tokioschool.filmapp.security.filter.FilmApiSecurityConfiguration;
import com.tokioschool.filmapp.services.artist.ArtistService;
import com.tokioschool.filmapp.services.jwt.JwtService;
import com.tokioschool.filmapp.services.movie.MovieService;
import com.tokioschool.filmapp.services.ratings.RatingFilmService;
import com.tokioschool.filmapp.services.user.UserService;
import com.tokioschool.redis.services.JwtBlacklistService;
import com.tokioschool.store.dto.ResourceContentDto;
import com.tokioschool.store.facade.StoreFacade;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(controllers = ResourceApiController.class) // obtiente solo el contexto del contraldor especificado
//@ActiveProfiles("test")
//@AutoConfigureMockMvc // Habilita MockMvc con filtros de seguridad
//@TestPropertySource(properties = {
//        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
//        "spring.jpa.hibernate.ddl-auto=create-drop",
//        "spring.datasource.driver-class-name=org.h2.Driver",
//        "spring.datasource.url=dbc:h2:file:../rating-bbdd/testdb",
//        "spring.datasource.username=sa",
//        "spring.datasource.password=password",
//        "logging.level.org.springframework.security=DEBUG",
//        "jwt.secret=secretos123123",
//        "jwt.expiration=PT1H"
//})
//@Import({FilmApiSecurityConfiguration.class,
//        JwtConfiguration.class}) // Importa la configuración de seguridad
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "jwt.secret=secretos123123",
        "jwt.expiration=PT1H",
        "spring.liquibase.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
})
public class ResourceApiControllerWithSecurityEnableUTest {

    @Autowired private MockMvc mockMvc;

//    // required for log filter
    @MockitoBean    private UserService userService;
//    @MockitoBean    private JwtBlacklistService jwtBlacklistService;
//    // service in controller
    @MockitoBean private StoreFacade storeFacade;

    @Test
    @WithMockUser(username = "user",roles = "user")
    public void givenValidResourceId_whenGetContentResourceHandler_thenReturnResourceContent() throws Exception {
        UUID resourceId = UUID.randomUUID();
        ResourceContentDto resourceContentDto = new ResourceContentDto(resourceId,"hola".getBytes(),"resource.txt","application/json", "description", "hola".length() );
        when(storeFacade.findResource(resourceId)).thenReturn(Optional.of(resourceContentDto));

        mockMvc.perform(get("/film/api/resources")
                        .param("resourceId", resourceId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().bytes(resourceContentDto.content()));
    }

    @Test
    @WithMockUser(username = "user",roles = "user")
    public void givenInvalidResourceId_whenGetContentResourceHandler_thenThrowNotFoundException() throws Exception {
        UUID resourceId = UUID.randomUUID();
        when(storeFacade.findResource(resourceId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/film/api/resources")
                        .param("resourceId", resourceId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user",roles = "user")
    public void givenValidResourceId_whenDownloadContentResourceHandler_thenReturnResourceContentWithHeaders() throws Exception {
        UUID resourceId = UUID.randomUUID();
        ResourceContentDto resourceContentDto = new ResourceContentDto(resourceId,"hola".getBytes(),"resource.txt","application/json", "description", "hola".length() );
        when(storeFacade.findResource(resourceId)).thenReturn(Optional.of(resourceContentDto));

        mockMvc.perform(get("/film/api/resources/download")
                        .param("resourceId", resourceId.toString()))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=resource.txt"))
                .andExpect(header().string(HttpHeaders.CONTENT_LENGTH, String.valueOf("hola".length())))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/json"))
                .andExpect(content().bytes(resourceContentDto.content()));
    }

    @Test
    @WithMockUser(username = "user",roles = "user")
    public void givenInvalidResourceId_whenDownloadContentResourceHandler_thenThrowNotFoundException() throws Exception {
        UUID resourceId = UUID.randomUUID();
        when(storeFacade.findResource(resourceId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/film/api/resources/download")
                        .param("resourceId", resourceId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    public void givenInvalidResourceIdAndUserAnonymous_whenDownloadContentResourceHandler_thenIsUnauthorized() throws Exception {
        UUID resourceId = UUID.randomUUID();
        when(storeFacade.findResource(resourceId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/film/api/resources/download")
                        .param("resourceId", resourceId.toString()))
                .andExpect(status().isUnauthorized());
    }
}
