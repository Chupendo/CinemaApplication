package com.tokioschool.filmweb.configs.internazionalitation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@DisplayName("LocaleInterceptorConfig Tests")
class LocaleInterceptorConfigUTest {


    @InjectMocks
    private LocaleInterceptorConfig config;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("Should return a CookieLocaleResolver instance")
    void shouldReturnCookieLocaleResolver() {
        LocaleResolver resolver = config.localeResolver();
        assertInstanceOf(CookieLocaleResolver.class, resolver);
    }

    @Test
    @DisplayName("Should return a LocaleChangeInterceptor with correct param")
    void shouldReturnLocaleChangeInterceptor() {
        LocaleChangeInterceptor interceptor = config.localeChangeInterceptor();
        assertEquals(LocaleInterceptorConfig.INTERCEPTOR_PARAM_NAME, interceptor.getParamName());
    }

    @Test
    @DisplayName("Should add localeChangeInterceptor to registry")
    void shouldAddInterceptorToRegistry() {
        InterceptorRegistry registry = mock(InterceptorRegistry.class);

        // Act
        config.addInterceptors(registry);

        // Assert
        verify(registry, times(1)).addInterceptor(any(LocaleChangeInterceptor.class));
    }
}