package com.tokioschool.ratingapp.services.jwt.configs;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * Configuración para la generación de claves RSA y JWK.
 *
 * Esta clase configura un bean que genera un conjunto de claves RSA y las expone
 * como un JWK (JSON Web Key) para su uso en la autenticación y autorización.
 *
 * Proporciona metodos para generar claves RSA y construir un JWKSource.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    /**
     * Genera un `JWKSource` que contiene un conjunto de claves RSA.
     *
     * Este metodo crea un par de claves RSA (pública y privada) y las encapsula
     * en un objeto `JWK`. El JWK se utiliza para firmar y verificar tokens JWT.
     *
     * @return Un `JWKSource` que contiene el conjunto de claves JWK.
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey(); // metodo para generar una clave RSA
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
     * Genera un par de claves RSA.
     *
     * Este metodo utiliza el generador de claves RSA para crear un par de claves
     * con un tamaño de 2048 bits. La clave generada se utiliza para firmar y verificar
     * tokens JWT.
     *
     * @return Un objeto `KeyPair` que contiene la clave pública y privada.
     * @throws IllegalStateException Si ocurre un error al generar la clave RSA.
     */
    private static KeyPair generateRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException("Error generando la clave RSA", ex);
        }
    }
}