package com.tokioschool.ratingapp.oauth.configurations;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.tokioschool.ratingapp.securities.jwt.properties.JwtConfigurationProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(JwtConfigurationProperty.class)
@RequiredArgsConstructor
public class JwtAuthConfig {



    /**
     * Esto permite generar las claves usadasa para obtener el JWK del servidor de Authenticación
     * @return
     */

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey(); // Metodo para generar una clave RSA
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        JWK jwk = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString()) // ID único para la clave
                //.expirationTime( Date.from(Instant.now().plusMillis(Duration.ofHours(1).toMillis()  + 1 )  ) )
                .build();

        return new ImmutableJWKSet<>(new JWKSet(jwk));
    }

    /**
     * Permite agregar claims personalidzas al token al token de autenticación server
     * Este bean se ejecuta antes de generar el token el servidor de authenticacion y añadir el usuario al contexto
     *
     * @param userDetailsService
     * @return
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(UserDetailsService userDetailsService) {
        return (context) -> {
            if (context.getPrincipal().getName().equals("oauth-client")) { // añade los roles y permisos personalizados
                UserDetails userDetails = userDetailsService.loadUserByUsername(context.getPrincipal().getName());
                Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

                // Extract scope from claims
                //List<String> scopes = context.getClaims().build().getClaimAsStringList("scope").stream().map("SCOPE_"::concat).toList();
                List<String> scopes = context.getClaims().build().getClaimAsStringList("scope");
                // Add authorities and scopes to claims
                context.getClaims().claims(claims -> {
                    claims.put("authorities", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
                    claims.put("scope", scopes);
                });
            } else {
                // row new AccessDeniedException("Non Authorized or Authenticated");
            }
        };
    }

    /**
     * Esto es requerido para decodfijia el json
     * @param jwkSource
     * @return
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    private static KeyPair generateRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException("Error generando la clave RSA", ex);
        }
    }


    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .build();
    }
}