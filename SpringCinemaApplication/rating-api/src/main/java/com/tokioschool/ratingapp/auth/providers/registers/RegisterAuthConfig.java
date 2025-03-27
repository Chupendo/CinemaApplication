package com.tokioschool.ratingapp.auth.providers.registers;


import com.tokioschool.ratingapp.auth.configs.OauthClientProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class RegisterAuthConfig {

    private final PasswordEncoder passwordEncoder;
    private final OauthClientProperty oauthClientProperty;

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        return new InMemoryRegisteredClientRepository(registerCredentialClient(),registerAuthenticationClient(),registerAuthenticationWebClient());
    }

    private RegisteredClient registerCredentialClient(){
        return RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId( oauthClientProperty.clientOauth().clientId() )
                //.clientSecret("{noop}secret3") // No cifrado (usar BCrypt en prod)
                .clientSecret(passwordEncoder.encode( oauthClientProperty.clientOauth().clientSecret()) )
                .clientAuthenticationMethod(org.springframework.security.oauth2.core.ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scopes(scopes->scopes.addAll( oauthClientProperty.clientOauth().scopes() ))
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(1))
                        .build())
                .clientSettings(ClientSettings.builder().build())
                .build();
    }

    private RegisteredClient registerAuthenticationClient(){
        // 1º En el navegador: http://127.0.0.1:9095/oauth2/authorize?response_type=code&client_id=oidc-client&redirect_uri=http://127.0.0.1:9095/login/oauth2/code/oidc-client&scope=openid
        /**
         * curl --location 'http://127.0.0.1:9095/oauth2/authorize?response_type=code&client_id=oidc-client&redirect_uri=http%3A%2F%2F127.0.0.1%3A9095%2Flogin%2Foauth2%2Fcode%2Foidc-client&scope=openid' \
         * --header 'Cookie: JSESSIONID=40E6E5C7EA8A71D58C65317EE2FE9CF8'
         */
        // 2º Login for get code
        // 3ª Post http://127.0.0.1:9095/oauth2/token
        // 3.1 Insert authenticaction basic and params body: grant_type, code and redirect
        /**
         * curl --location 'http://127.0.0.1:9095/oauth2/token' \
         * --header 'Authorization: ••••••' \
         * --header 'Cookie: JSESSIONID=40E6E5C7EA8A71D58C65317EE2FE9CF8' \
         * --form 'grant_type="authorization_code"' \
         * --form 'code="7ZsiI1PqSzvfzQf5cWj39YJLiFcpYnwqAVy1kGnSFF-wxBR8-v45W-80zUVwZ48dbdPPqh0E9VD5zn30ovVintGkKKDzjE_c3MiFW8WwVj6a_-MYOrXnotK_v_xPd8oQ"' \
         * --form 'redirect_uri="http://127.0.0.1:9095/login/oauth2/code/oidc-client"'
         */
        return RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId( oauthClientProperty.clientOidc().clientId() )
                .clientSecret(passwordEncoder.encode("secret3"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri( oauthClientProperty.clientOidc().redirectUri() )
                .redirectUri( oauthClientProperty.clientOidc().authorizationUri() )
                .postLogoutRedirectUri("http://127.0.0.1:9095/")
//                .scope(OidcScopes.OPENID)
//                .scope(OidcScopes.PROFILE)
                .scopes( scopes -> scopes.addAll( oauthClientProperty.clientOidcWeb().scopes() ))
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
                .build();
    }

    private RegisteredClient registerAuthenticationWebClient(){
        // This client, get token form request (required auth), en el navegador: http://127.0.0.1:9095/oauth2/authorize?response_type=code&client_id=oidc-client&redirect_uri=http://127.0.0.1:9095/login/oauth2/code/oidc-client&scope=openid
        return RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId( oauthClientProperty.clientOidcWeb().clientId() )
                .clientSecret(passwordEncoder.encode( oauthClientProperty.clientOidcWeb().clientSecret() ))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri( oauthClientProperty.clientOidcWeb().redirectUri() )
                .redirectUri( oauthClientProperty.clientOidcWeb().authorizationUri() )
                .postLogoutRedirectUri( oauthClientProperty.clientOidcWeb().postLogoutRedirectUri() )
//                .scope(OidcScopes.OPENID)
//                .scope(OidcScopes.PROFILE)
                .scopes( scopes -> scopes.addAll( oauthClientProperty.clientOidcWeb().scopes() ))
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
                .build();
    }
}
