package com.tokioschool.filmweb.configs.binding;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("FilmWebLocalValidatorFactoryBeanConfiguration Tests")
@ActiveProfiles("test")
class FilmWebLocalValidatorFactoryBeanConfigurationUTest {


    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private FilmWebLocalValidatorFactoryBeanConfiguration configuration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("localValidatorFactoryBean Method")
    class LocalValidatorFactoryBeanMethod {

        @Test
        @DisplayName("Should return a configured LocalValidatorFactoryBean instance")
        void shouldReturnConfiguredLocalValidatorFactoryBeanInstance() {
            LocalValidatorFactoryBean validatorFactoryBean = configuration.localValidatorFactoryBean();

            assertNotNull(validatorFactoryBean);
        }

        @Test
        @DisplayName("Should handle null MessageSource gracefully")
        void shouldHandleNullMessageSourceGracefully() {
            configuration = new FilmWebLocalValidatorFactoryBeanConfiguration(null);

            LocalValidatorFactoryBean validatorFactoryBean = configuration.localValidatorFactoryBean();

            assertNotNull(validatorFactoryBean);
        }
    }
}