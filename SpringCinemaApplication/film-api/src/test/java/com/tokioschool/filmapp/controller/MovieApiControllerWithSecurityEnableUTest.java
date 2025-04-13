package com.tokioschool.filmapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.movie.MovieDto;
import com.tokioschool.filmapp.security.confings.JwtConfiguration;
import com.tokioschool.filmapp.records.SearchMovieRecord;
import com.tokioschool.filmapp.security.filter.FilmApiSecurityConfiguration;
import com.tokioschool.filmapp.services.movie.MovieService;
import com.tokioschool.filmapp.services.user.UserService;
import com.tokioschool.redis.services.JwtBlacklistService;
import com.tokioschool.store.facade.StoreFacade;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
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


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "logging.level.org.springframework.security=DEBUG",
        "jwt.secret=secretos123123",
        "jwt.expiration=PT1H"
})
class MovieApiControllerWithSecurityEnableUTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private MovieService movieService;
    @MockitoBean
    private StoreFacade storeFacade;

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

    @Test
    @Order(2)
    @WithMockUser(username = "user",roles = "user")
    void givenRequest_whenFindMovieByIdHandler_returnOk() throws Exception {
        // Mockear el servicio
        Mockito.when(movieService.getMovieById(1L)).thenReturn(MovieDto.builder().build());

        // Realizar la solicitud POST y verificar la respuesta
        mockMvc.perform(MockMvcRequestBuilders.get("/film/api/movies/{id}",1L) // Ajusta el endpoint si es necesario
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }
}