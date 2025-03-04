package com.tokioschool.ratingapp.securities.configurations.authentication;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AuthenticationManagerConfigurationUTest {

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @InjectMocks
    private AuthenticationManagerConfiguration authenticationManagerConfiguration;

    @Test
    void authenticationManager_returnsAuthenticationManager() throws Exception {
        AuthenticationManager expectedManager = Mockito.mock(AuthenticationManager.class);
        Mockito.when(authenticationConfiguration.getAuthenticationManager()).thenReturn(expectedManager);

        AuthenticationManager authenticationManager = authenticationManagerConfiguration.authenticationManager(authenticationConfiguration);

        assertThat(authenticationManager).isEqualTo(expectedManager);
    }

    @Test
    void authenticationManager_throwsException_whenConfigurationFails() throws Exception {
        Mockito.when(authenticationConfiguration.getAuthenticationManager()).thenThrow(new Exception("Configuration failed"));

        assertThrows(Exception.class, () -> authenticationManagerConfiguration.authenticationManager(authenticationConfiguration));
    }
}