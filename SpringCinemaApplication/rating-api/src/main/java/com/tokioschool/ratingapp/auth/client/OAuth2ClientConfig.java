package com.tokioschool.ratingapp.auth.client;

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

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(clientRegistration());
    }

    @Bean
    public ClientRegistration clientRegistration() {
        return ClientRegistration.withRegistrationId("oauth-client")
                .clientId("oauth-client")
                .clientSecret(passwordEncoder.encode("secret3"))
                .authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri("http://127.0.0.1:9095/login/oauth2/code/oauth-client")
                .scope("openid", "profile", "read","writer")
                .authorizationUri("http://127.0.0.1:9095/oauth2/authorize")
                .tokenUri("http://127.0.0.1:9095/oauth2/token")
                .userInfoUri("http://127.0.0.1:9095/userinfo")
                .userNameAttributeName("sub")
                .build();
    }
}