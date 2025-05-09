package com.tokioschool.filmapp.security.confings;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.tokioschool.jwt.properties.JwtProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.spec.SecretKeySpec;

/**
 * Configuración de JWT para codificación y decodificación de tokens.
 *
 * Esta clase proporciona la configuración necesaria para manejar la seguridad basada en JWT
 * utilizando el algoritmo HMAC-SHA256. Define beans para codificar y decodificar tokens JWT.
 *
 * Anotaciones:
 * - {@link Configuration}: Indica que esta clase es una configuración de Spring.
 * - {@link RequiredArgsConstructor}: Genera un constructor con los argumentos requeridos para los
 *   campos finales.
 *
 * Dependencias:
 * - {@link JwtProperty}: Clase que contiene las propiedades de configuración de JWT, como la clave secreta.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
@RequiredArgsConstructor
public class JwtConfiguration {

    /**
     * Propiedades de configuración de JWT.
     *
     * Este campo contiene información como la clave secreta utilizada para firmar y validar los tokens JWT.
     */
    private final JwtProperty jwtProperty;

    /**
     * Bean para decodificar tokens JWT.
     *
     * Este metodo crea un bean de tipo {@link NimbusJwtDecoder} que utiliza la clave secreta configurada
     * para validar y decodificar los tokens JWT firmados con HMAC-SHA256.
     *
     * @return una instancia de {@link NimbusJwtDecoder} configurada con HMAC-SHA256.
     */
    @Bean
    public NimbusJwtDecoder hmacJwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(
                        new SecretKeySpec(jwtProperty.secret().getBytes(), "HMACSHA256"))
                .build();
    }

    /**
     * Bean para codificar tokens JWT.
     *
     * Este metodo crea un bean de tipo {@link NimbusJwtEncoder} que utiliza la clave secreta configurada
     * para firmar los tokens JWT con el algoritmo HMAC-SHA256.
     *
     * @return una instancia de {@link NimbusJwtEncoder} configurada con HMAC-SHA256.
     */
    @Bean
    public NimbusJwtEncoder nimbusJwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(
                new SecretKeySpec(jwtProperty.secret().getBytes(), "HMACSHA256")
        ));
    }
}