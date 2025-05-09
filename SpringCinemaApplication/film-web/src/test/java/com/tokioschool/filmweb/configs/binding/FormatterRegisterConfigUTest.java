package com.tokioschool.filmweb.configs.binding;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.format.FormatterRegistry;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@DisplayName("FormatterRegisterConfig Tests")
class FormatterRegisterConfigUTest {

    @InjectMocks
    private FormatterRegisterConfig formatterRegisterConfig;

    private FormatterRegistry registry;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registry = mock(FormatterRegistry.class);
    }

    @Nested
    @DisplayName("addFormatters Method")
    class AddFormattersMethod {

        @Test
        @DisplayName("Should add custom formatters to the registry")
        void shouldAddCustomFormattersToRegistry() {
            formatterRegisterConfig.addFormatters(registry);

            verify(registry, atLeastOnce()).addFormatterForFieldType(any(), any());
        }

        @Test
        @DisplayName("Should throw exception when registry is null")
        void shouldThrowExceptionWhenRegistryIsNull() {
            Assertions.assertThatThrownBy(() -> formatterRegisterConfig.addFormatters(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}