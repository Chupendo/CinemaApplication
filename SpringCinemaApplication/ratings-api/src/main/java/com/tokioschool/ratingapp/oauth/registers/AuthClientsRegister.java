package com.tokioschool.ratingapp.oauth.registers;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
public class AuthClientsRegister {

    @Bean
    public RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder) {
        // create a client oauth
        RegisteredClient oauthClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("oauth-client")
                .clientSecret(passwordEncoder.encode("secret3"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS) // This not support refresh token, this recommend for auth between server without users
                // scopes for client
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("read")
                .scope("write")
                // configuration token custom
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(60))  // Access Token, valid 60 min
                        .refreshTokenTimeToLive(Duration.ofDays(7))    // Refresh Token, valid 7 días
                        .reuseRefreshTokens(false)  // If is true, then utilization the refresh token same
                        .build())
                .build();

        // register all clients
        //return new InMemoryRegisteredClientRepository(oauthClient,genericOauthClient);
        return new InMemoryRegisteredClientRepository(oauthClient);
    }
}
