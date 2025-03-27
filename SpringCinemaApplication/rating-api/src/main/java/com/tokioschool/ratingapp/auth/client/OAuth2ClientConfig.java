package com.tokioschool.ratingapp.auth.client;

import com.tokioschool.ratingapp.auth.configs.OauthClientProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class OAuth2ClientConfig {



    private final PasswordEncoder passwordEncoder;
    private final OauthClientProperty oauthClientProperty;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {

        return new InMemoryClientRegistrationRepository(clientCredentialsRegistration(),clientAuthorizationRegistration(),clientAuthorizationWebRegistration());
    }


    public ClientRegistration clientCredentialsRegistration() {
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

    public ClientRegistration clientAuthorizationRegistration() {
        return ClientRegistration.withRegistrationId( UUID.randomUUID().toString())
                .clientId("oauth-client")
                .clientSecret(passwordEncoder.encode("secret3"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://127.0.0.1:9095/login/oauth2/code/oidc-client")
                .authorizationUri("http://127.0.0.1:9095/oauth2/authorize")
                .tokenUri("http://127.0.0.1:9095/oauth2/token")
                .scope("openid")
                .scope("read")
                .build();
    }

    public ClientRegistration clientAuthorizationWebRegistration() {
        return ClientRegistration.withRegistrationId( UUID.randomUUID().toString())
                .clientId("oidc-client-web")
                .clientSecret(passwordEncoder.encode("secret3"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://127.0.0.1:9095/login/oauth2/code/oidc-client-web")
                .authorizationUri("http://127.0.0.1:9095/oauth2/authorize")
                .tokenUri("http://127.0.0.1:9095/oauth2/token")
                .scope("openid")
                .scope("read")
                .build();
    }

}