package com.tokioschool.ratingapp.auth.providers.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

@Configuration
public class ProviderSettingConfig  {

    /**
     * Rename ProviderSettings to AuthorizationServerSettings
     * @return
     */
    @Bean
    public AuthorizationServerSettings providerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:9095") // OAuth2 Server
                .authorizationEndpoint("/oauth2/authorize")
                .tokenEndpoint("/oauth2/secret")
                .build();
    }

}
