package com.tokioschool.ratingapp.securities.jwt.configuration;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.tokioschool.ratingapp.securities.jwt.properties.JwtConfigurationProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.spec.SecretKeySpec;

/**
 * Configuration class for setting up JWT encoder and decoder beans.
 */
@Configuration
@EnableConfigurationProperties(JwtConfigurationProperty.class)
@RequiredArgsConstructor
public class JwtConfiguration {

    private final JwtConfigurationProperty jwtConfigurationProperty;

    /**
     * Creates a NimbusJwtEncoder bean.
     * This bean is used to encode JWTs using the HMAC algorithm.
     *
     * @return the NimbusJwtEncoder instance
     */
    @Bean
    public NimbusJwtEncoder nimbusJwtEncoder() {
        // The source type will be a secret encoded with HMAC
        return new NimbusJwtEncoder(new ImmutableSecret<>(
                new SecretKeySpec(jwtConfigurationProperty.secret().getBytes(),"HMAC")
        ));
    }

    /**
     * Creates a NimbusJwtDecoder bean.
     * This bean is used to decode JWTs using the HMAC algorithm.
     *
     * @return the NimbusJwtDecoder instance
     */
    @Bean
    public NimbusJwtDecoder nimbusJwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(
                new SecretKeySpec(jwtConfigurationProperty.secret().getBytes(),"HMAC")).build();
    }
}