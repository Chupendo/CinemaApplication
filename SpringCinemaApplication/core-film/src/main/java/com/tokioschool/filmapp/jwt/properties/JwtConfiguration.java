package com.tokioschool.filmapp.jwt.properties;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableConfigurationProperties(JwtConfigurationProperty.class)
@RequiredArgsConstructor
public class JwtConfiguration {

    private final JwtConfigurationProperty jwtConfigurationProperty;

    /**
     * Codificador de JWT
     * @return
     */
    @Bean
    public NimbusJwtEncoder nimbusJwtEncoder() {
        // tipo source: sera un secreto codifcado con HMAC
        return new NimbusJwtEncoder(new ImmutableSecret<>(
                new SecretKeySpec(jwtConfigurationProperty.secret().getBytes(),"HMAC")
        ));
    }

    /**
     * Decoficiador de JWT
     */
    @Bean
    public NimbusJwtDecoder nimbusJwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(
                new SecretKeySpec(jwtConfigurationProperty.secret().getBytes(),"HMAC")).build();
    }
}
