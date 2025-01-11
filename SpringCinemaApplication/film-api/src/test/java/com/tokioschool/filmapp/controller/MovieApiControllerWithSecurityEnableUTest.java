package com.tokioschool.filmapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.movie.MovieDto;
import com.tokioschool.filmapp.jwt.properties.JwtConfiguration;
import com.tokioschool.filmapp.records.SearchMovieRecord;
import com.tokioschool.filmapp.security.filter.FilmApiSecurityConfiguration;
import com.tokioschool.filmapp.services.movie.MovieService;
import com.tokioschool.filmapp.services.user.UserService;
import com.tokioschool.redis.services.JwtBlacklistService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@WebMvcTest(controllers = MovieApiController.class) // obtiente solo el contexto del contraldor especificado
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc // Habilita MockMvc con filtros de seguridad
@TestPropertySource(properties = {
        "logging.level.org.springframework.security=DEBUG",
        "application.jwt.secret=secretos123123",
        "application.jwt.expiration=PT1H"
})
@Import({FilmApiSecurityConfiguration.class, JwtConfiguration.class}) // Importa la configuración de seguridad

class MovieApiControllerWithSecurityEnableUTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private MovieService movieService;

    // dependencies required for be used in filters chains
    @MockitoBean    private UserService userService;
    @MockitoBean    private JwtBlacklistService jwtBlacklistService;

    @Test
    @Order(1)
    @WithMockUser(username = "user",roles = "user")
    void givenRequest_whenSearchMoviesHandler_returnPage() throws Exception {
        // Mockear el servicio
        PageDTO<MovieDto> pageDTO = PageDTO.<MovieDto>builder().build();
        Mockito.when(movieService.searchMovie(Mockito.any(SearchMovieRecord.class))).thenReturn(pageDTO);

        // Realizar la solicitud POST y verificar la respuesta
        mockMvc.perform(MockMvcRequestBuilders.get("/film/api/movies") // Ajusta el endpoint si es necesario
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }
}