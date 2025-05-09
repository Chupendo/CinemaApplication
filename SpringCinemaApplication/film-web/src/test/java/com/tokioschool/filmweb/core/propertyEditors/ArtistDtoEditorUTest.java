package com.tokioschool.filmweb.core.propertyEditors;

import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.services.artist.ArtistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@DisplayName("ArtistDtoEditor Tests")
@ActiveProfiles("test")
class ArtistDtoEditorUTest {

    @Mock
    private ArtistService artistService;

    @InjectMocks
    private ArtistDtoEditor artistDtoEditor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("setAsText Method")
    class SetAsText {

        @Test
        @DisplayName("Should set ArtistDto when valid ID is provided")
        void shouldSetArtistDtoWhenValidIdProvided() {
            String validId = "123";
            ArtistDto expectedArtist = new ArtistDto();
            when(artistService.findById(123L)).thenReturn(expectedArtist);

            artistDtoEditor.setAsText(validId);

            assertSame(expectedArtist, artistDtoEditor.getValue());
            verify(artistService).findById(123L);
        }

        @Test
        @DisplayName("Should set null when text is null")
        void shouldSetNullWhenTextIsNull() {
            artistDtoEditor.setAsText(null);

            assertNull(artistDtoEditor.getValue());
            verifyNoInteractions(artistService);
        }

        @Test
        @DisplayName("Should set null when text is empty")
        void shouldSetNullWhenTextIsEmpty() {
            artistDtoEditor.setAsText("");

            assertNull(artistDtoEditor.getValue());
            verifyNoInteractions(artistService);
        }

        @Test
        @DisplayName("Should set null when text is not a number")
        void shouldSetNullWhenTextIsNotANumber() {
            artistDtoEditor.setAsText("invalid");

            assertNull(artistDtoEditor.getValue());
            verifyNoInteractions(artistService);
        }

        @Test
        @DisplayName("Should set null when artist is not found")
        void shouldSetNullWhenArtistNotFound() {
            String validId = "123";
            when(artistService.findById(123L)).thenReturn(null);

            artistDtoEditor.setAsText(validId);

            assertNull(artistDtoEditor.getValue());
            verify(artistService).findById(123L);
        }
    }
}