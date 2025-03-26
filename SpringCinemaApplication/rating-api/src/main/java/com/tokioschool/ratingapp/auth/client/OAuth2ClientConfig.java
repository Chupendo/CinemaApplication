package com.tokioschool.ratingapp.auth.client;

import com.tokioschool.ratingapp.auth.configs.OauthClientProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

@Configuration
@RequiredArgsConstructor
public class OAuth2ClientConfig {



    private final PasswordEncoder passwordEncoder;
    private final OauthClientProperty oauthClientProperty;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(clientRegistration());
    }

    @Bean
    public ClientRegistration clientRegistration() {
        return ClientRegistration.withRegistrationId( oauthClientProperty.clientId() )
                .clientId( oauthClientProperty.clientId() )
                .clientSecret(passwordEncoder.encode( oauthClientProperty.clientSecret() ))
                .authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri(oauthClientProperty.redirectUri() )
                //.scope("openid", "profile", "read","writer")
                .scope( oauthClientProperty.scopes() )
                .authorizationUri(oauthClientProperty.authorizationUri() )
                .tokenUri( oauthClientProperty.tokenUri() )
                .userInfoUri( oauthClientProperty.userInfoUri() )
                .userNameAttributeName( oauthClientProperty.userNameAttributeName() )
                .build();
    }
}