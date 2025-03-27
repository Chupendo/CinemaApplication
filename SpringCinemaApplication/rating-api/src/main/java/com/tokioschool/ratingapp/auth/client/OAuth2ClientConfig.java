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
        return ClientRegistration.withRegistrationId( oauthClientProperty.clientOauth().clientId() )
                .clientId( oauthClientProperty.clientOauth().clientId() )
                .clientSecret(passwordEncoder.encode( oauthClientProperty.clientOauth().clientSecret() ))
                .authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri(oauthClientProperty.clientOauth().redirectUri() )
                //.scope("openid", "profile", "read","writer")
                .scope( oauthClientProperty.clientOauth().scopes() )
                .authorizationUri(oauthClientProperty.clientOauth().authorizationUri() )
                .tokenUri( oauthClientProperty.clientOauth().tokenUri() )
                .userInfoUri( oauthClientProperty.clientOauth().userInfoUri() )
                .userNameAttributeName( oauthClientProperty.clientOauth().userNameAttributeName() )
                .build();
    }

    public ClientRegistration clientAuthorizationRegistration() {
        return ClientRegistration.withRegistrationId( UUID.randomUUID().toString())
                .clientId( oauthClientProperty.clientOidc().clientId() )
                .clientSecret( passwordEncoder.encode( oauthClientProperty.clientOidc().clientSecret() ))
                .clientAuthenticationMethod( ClientAuthenticationMethod.CLIENT_SECRET_BASIC )
                .authorizationGrantType( AuthorizationGrantType.AUTHORIZATION_CODE )
                .authorizationGrantType( AuthorizationGrantType.REFRESH_TOKEN )
                .redirectUri( oauthClientProperty.clientOidc().redirectUri() )
                .authorizationUri( oauthClientProperty.clientOidc().authorizationUri() )
                .tokenUri( oauthClientProperty.clientOidc().tokenUri() )
                //.scope("openid")
                //.scope("read")
                .scope( oauthClientProperty.clientOidc().scopes() )
                .build();
    }

    public ClientRegistration clientAuthorizationWebRegistration() {
        return ClientRegistration.withRegistrationId( UUID.randomUUID().toString())
                .clientId( oauthClientProperty.clientOidcWeb().clientId() )
                .clientSecret(passwordEncoder.encode( oauthClientProperty.clientOidcWeb().clientSecret() ) )
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri( oauthClientProperty.clientOidcWeb().redirectUri() )
                .authorizationUri( oauthClientProperty.clientOidcWeb().authorizationUri() )
                .tokenUri( oauthClientProperty.clientOidcWeb().tokenUri() )
                //.scope("openid")
                //.scope("read")
                .scope( oauthClientProperty.clientOidcWeb().scopes() )
                .build();
    }

}