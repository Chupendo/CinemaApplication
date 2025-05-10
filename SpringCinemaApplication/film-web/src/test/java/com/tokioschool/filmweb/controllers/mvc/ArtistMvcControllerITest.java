package com.tokioschool.filmweb.controllers.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.services.artist.ArtistService;
import com.tokioschool.filmapp.services.user.UserService;
import com.tokioschool.securities.filters.LogRequestFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Profile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(controllers = ArtistMvcController.class)
@ActiveProfiles("test")
class ArtistMvcControllerITest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArtistService artistService;

    @MockitoBean
    private UserService userService;

    private ArtistDto artistDto;

    @BeforeEach
    void setup() {
        artistDto = ArtistDto.builder().id(1L).name("Test").surname("Artist").build();
    }

    @Test
    @DisplayName("GET /web/artists/list returns view with artists page")
    @WithMockUser
    void listPageArtis_ShouldReturnArtistListView() throws Exception {
        PageDTO<ArtistDto> mockPageDTO = PageDTO.<ArtistDto>builder()
                .items(Collections.emptyList())
                .pageSize(1)
                .pageNumber(1)
                .totalPages(1)
                .build();

        when(artistService.searchArtist(anyInt(), anyInt(), any()))
                .thenReturn(mockPageDTO);

        mockMvc.perform(get("/web/artists/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("artists/list"))
                .andExpect(model().attributeExists("pageArtistDto"));
    }

    @Test
    @DisplayName("GET /web/artists/profile/{id} returns profile view with artist")
    @WithMockUser
    void profileHandler_ShouldReturnArtistProfileView() throws Exception {
        when(artistService.findById(1L)).thenReturn(artistDto);

        mockMvc.perform(get("/web/artists/profile/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("artists/form"))
                .andExpect(model().attributeExists("artist"))
                .andExpect(model().attribute("profileMode", true))
                .andExpect(model().attribute("isRegister", false));
    }

    @Test
    @DisplayName("GET /web/artists/form with no ID returns creation form")
    @WithMockUser
    void artistCreateOrEditHandler_ShouldReturnCreateForm() throws Exception {
        mockMvc.perform(get("/web/artists/form"))
                .andExpect(status().isOk())
                .andExpect(view().name("artists/form"))
                .andExpect(model().attribute("isRegister", true));
    }

    @Test
    @DisplayName("POST /web/artists/form without errors registers artist")
    @WithMockUser
    void formRegisterOrEditArtist_ShouldRegisterArtist() throws Exception {
        mockMvc.perform(post("/web/artists/form")
                        .with(csrf())
                        .param("name", "Updated")
                        .param("surname", "Artist")
                        .param("typeArtist", "ACTOR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/artists/list"));

        verify(artistService).registerArtist(any(ArtistDto.class));
    }

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /web/artists/form/{id} updates artist")
    @WithMockUser(roles = "ADMIN")
    void formRegisterOrEditArtist_ShouldUpdateArtist() throws Exception {

        mockMvc.perform(post("/web/artists/form/1")
                        .with(csrf())
                        .param("id", "1")
                        .param("name", "Updated")
                        .param("surname", "Artist")
                        .param("typeArtist", "ACTOR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/artists/list"));

        verify(artistService).updatedArtist(eq(1L), any(ArtistDto.class));
    }
}
