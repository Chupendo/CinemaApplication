package com.tokioschool.filmweb.helpers;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.context.ActiveProfiles;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class TranslatedMessageHelperUTest {
    @Mock
    private ResourceBundleMessageSource resourceBundleMessageSource;

    private TranslatedMessageHelper translatedMessageHelper;

    @BeforeEach
    void setUp() {
        // Inicializa los mocks
        MockitoAnnotations.openMocks(this);
        // Crea la instancia del helper, pasando el ResourceBundleMessageSource mockeado
        translatedMessageHelper = new TranslatedMessageHelper(resourceBundleMessageSource);
    }

    @Test
    @DisplayName("Debe retornar el mensaje traducido cuando se pasa una clave de mensaje válida con locale especificado")
    void getMessage_ShouldReturnMessageWhenKeyIsFound() {
        // Preparar el comportamiento del mock
        String key = "greeting";
        String expectedMessage = "Hello, World!";
        Locale locale = Locale.ENGLISH;

        when(resourceBundleMessageSource.getMessage(key, null, StringUtils.EMPTY, locale)).thenReturn(expectedMessage);

        // Llamar al metodo
        String message = translatedMessageHelper.getMessage(key, locale, null);

        // Verificar que el mensaje es el esperado
        assertEquals(expectedMessage, message);
        // Verificamos que el metodo del mock fue llamado con los parámetros correctos
        verify(resourceBundleMessageSource).getMessage(key, null, StringUtils.EMPTY, locale);
    }

    @Test
    @DisplayName("Debe retornar el mensaje traducido con parámetros cuando se pasa una clave de mensaje con parámetros")
    void getMessage_ShouldReturnMessageWithParamsWhenKeyIsFound() {
        // Preparar el comportamiento del mock
        String key = "welcome";
        String expectedMessage = "Welcome, John!";
        Object[] params = {"John"};
        Locale locale = Locale.ENGLISH;

        when(resourceBundleMessageSource.getMessage(key, params, StringUtils.EMPTY, locale)).thenReturn(expectedMessage);

        // Llamar al metodo
        String message = translatedMessageHelper.getMessage(key, locale, params);

        // Verificar que el mensaje es el esperado con el parámetro
        assertEquals(expectedMessage, message);
        // Verificamos que el metodo del mock fue llamado con los parámetros correctos
        verify(resourceBundleMessageSource).getMessage(key, params, StringUtils.EMPTY, locale);
    }

    @Test
    @DisplayName("Debe usar el locale predeterminado si el locale se pasa como nulo")
    void getMessage_ShouldUseDefaultLocaleWhenLocaleIsNull() {
        try (MockedStatic<LocaleContextHolder> localeContextHolderMock = mockStatic(LocaleContextHolder.class)) {
            // Preparar el comportamiento del mock para el locale predeterminado
            String key = "goodbye";
            String expectedMessage = "Goodbye!";
            Locale defaultLocale = Locale.of("es","ES");

            // Mockear LocaleContextHolder para devolver el locale predeterminado
            localeContextHolderMock.when(LocaleContextHolder::getLocale).thenReturn(defaultLocale);

            // Aseguramos que el mock responda correctamente cuando el locale sea el predeterminado
            when(resourceBundleMessageSource.getMessage(key, null, StringUtils.EMPTY, defaultLocale))
                    .thenReturn(expectedMessage);

            // Llamar al metodo con un locale nulo
            String message = translatedMessageHelper.getMessage(key, null, null);

            // Verificar que el mensaje es el esperado
            assertEquals(expectedMessage, message);

            // Verificamos que el metodo del mock fue llamado con el locale predeterminado
            verify(resourceBundleMessageSource).getMessage(key, null, StringUtils.EMPTY, defaultLocale);
        }
    }

    @Test
    @DisplayName("Debe retornar una cadena vacía si no se encuentra el mensaje para la clave especificada")
    void getMessage_ShouldReturnEmptyStringIfMessageNotFound() {
        // Preparar el comportamiento del mock para simular una clave no encontrada
        String key = "notFoundKey";
        Locale locale = Locale.ENGLISH;

        when(resourceBundleMessageSource.getMessage(key, null, StringUtils.EMPTY, locale)).thenReturn(StringUtils.EMPTY);

        // Llamar al metodo
        String message = translatedMessageHelper.getMessage(key, locale, null);

        // Verificar que el mensaje sea una cadena vacía si no se encuentra la clave
        assertEquals(StringUtils.EMPTY, message);
    }
}