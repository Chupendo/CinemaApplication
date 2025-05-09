package com.tokioschool.ratingapp.auth.configs;


import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class JwtAuthConfig {





    /**
     * Permite agregar claims personalidzas al secret al secret de autenticación server
     * Este bean se ejecuta antes de generar el secret el servidor de authenticacion y añadir el usuario al contexto
     *
     * @param userDetailsService
     * @return
     */
//    @Bean
//    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(UserDetailsService userDetailsService) {
//        return (context) -> {
//            if (context.getPrincipal().getName().equals("oauth-client")) { // añade los roles y permisos personalizados
//                UserDetails userDetails = userDetailsService.loadUserByUsername(context.getPrincipal().getName());
//                Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
//
//                // Extract scope from claims
//                //List<String> scopes = context.getClaims().build().getClaimAsStringList("scope").stream().map("SCOPE_"::concat).toList();
//                List<String> scopes = context.getClaims().build().getClaimAsStringList("scope");
//                // Add authorities and scopes to claims
//                context.getClaims().claims(claims -> {
//                    claims.put("authorities", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
//                    claims.put("scope", scopes);
//                });
//            } else {
//                // row new AccessDeniedException("Non Authorized or Authenticated");
//            }
//        };
//    }

    /**
     * Esto es requerido para decodfijia el json
     * @param jwkSource
     * @return
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }


    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .build();
    }

}