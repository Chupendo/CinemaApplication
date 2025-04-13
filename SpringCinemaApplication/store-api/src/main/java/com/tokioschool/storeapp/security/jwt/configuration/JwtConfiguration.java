package com.tokioschool.storeapp.security.jwt.configuration;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.tokioschool.storeapp.security.jwt.properties.JwtConfigurationProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.spec.SecretKeySpec;

/**
 * Configuración para la codificación y decodificación de tokens JWT.
 *
 * Esta clase define los beans necesarios para codificar y decodificar tokens JWT
 * utilizando un secreto basado en HMAC. La configuración del secreto se obtiene
 * de las propiedades definidas en `JwtConfigurationProperty`.
 *
 * Notas:
 * - Es anotada con `@Configuration` para indicar que es una clase de configuración de Spring.
 * - Habilita las propiedades de configuración de JWT mediante `@EnableConfigurationProperties`.
 *
 * @version andres.rpenuela
 * @version 1.0
 */
@Configuration
@EnableConfigurationProperties(JwtConfigurationProperty.class)
@RequiredArgsConstructor
public class JwtConfiguration {

    private final JwtConfigurationProperty jwtConfigurationProperty;

    /**
     * Define un bean para codificar tokens JWT.
     *
     * Este metodo crea un codificador de JWT utilizando un secreto basado en HMAC.
     * El secreto se obtiene de las propiedades configuradas en `JwtConfigurationProperty`.
     *
     * @return Una instancia de `NimbusJwtEncoder` para codificar tokens JWT.
     */
    @Bean
    public NimbusJwtEncoder nimbusJwtEncoder() {
        // Tipo source: será un secreto codificado con HMAC
        return new NimbusJwtEncoder(new ImmutableSecret<>(
                new SecretKeySpec(jwtConfigurationProperty.secret().getBytes(), "HMAC")
        ));
    }

    /**
     * Define un bean para decodificar tokens JWT.
     *
     * Este metodo crea un decodificador de JWT utilizando un secreto basado en HMAC.
     * El secreto se obtiene de las propiedades configuradas en `JwtConfigurationProperty`.
     *
     * @return Una instancia de `NimbusJwtDecoder` para decodificar tokens JWT.
     */
    @Bean
    public NimbusJwtDecoder nimbusJwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(
                new SecretKeySpec(jwtConfigurationProperty.secret().getBytes(), "HMAC")).build();
    }
}