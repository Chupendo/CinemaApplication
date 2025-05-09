package com.tokioschool.filmweb.configs.internazionalitation;

import com.transferwise.icu.ICUMessageSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@DisplayName("MessageSourceConfiguration Tests")
class MessageSourceConfigurationUTest {

    private MessageSourceConfiguration configuration;

    @BeforeEach
    void setUp() {
        configuration = new MessageSourceConfiguration();
    }

    @Test
    @DisplayName("Should create a MessageSource bean with ICU parent")
    void shouldCreateMessageSourceWithIcuParent() {
        MessageSource messageSource = configuration.messageSource();
        assertNotNull(messageSource);
        assertInstanceOf(ResourceBundleMessageSource.class, messageSource);

        ResourceBundleMessageSource resourceBundle = (ResourceBundleMessageSource) messageSource;
        MessageSource parent = resourceBundle.getParentMessageSource();
        assertNotNull(parent);
        assertInstanceOf(ICUMessageSource.class, parent);
    }

    @Test
    @DisplayName("Should create an ICUMessageSource bean")
    void shouldCreateIcuMessageSource() {
        ICUMessageSource icuSource = configuration.icuMessageSource();
        assertNotNull(icuSource);
        assertInstanceOf(ICUMessageSource.class, icuSource);
    }
}