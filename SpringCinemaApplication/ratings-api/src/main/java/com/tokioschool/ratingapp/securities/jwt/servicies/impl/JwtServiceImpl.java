package com.tokioschool.ratingapp.securities.jwt.servicies.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.tokioschool.ratingapp.securities.jwt.properties.JwtConfigurationProperty;
import com.tokioschool.ratingapp.securities.jwt.servicies.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JWKSource<SecurityContext> jwkSource;
    private final JwtConfigurationProperty jwtConfigurationProperty;


    public SignedJWT generateToken(UserDetails userDetails) {
        // Create a JWKSelector
        JWKSelector jwkSelector = new JWKSelector(new JWKMatcher.Builder().build());

        // Claims (payload): a declaración o información sobre un usuario o entidad (estandar + personalizada)
        //List<String> authorities =  getAuthoritiesFromUserDetails(userDetails);

        try {
            JWK jwk  = jwkSource.get(jwkSelector, null).getFirst();

            // Obtain the private key from JWKSource
            RSAKey rsaKey  = (RSAKey) jwk;

            RSAPrivateKey privateKey = rsaKey.toRSAPrivateKey();

            // Crear los claims del token
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userDetails.getUsername())
                    .issuer("http://127.0.0.1:9095") // Servidor emisor
                    .issueTime(Date.from(Instant.now()))
                    .expirationTime( Date.from(Instant.now().plusMillis(jwtConfigurationProperty.expiration().toMillis()  +1 ) ) ) // Expira en 1 hora
                    .claim("authorities",
                            userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()) // claim personalizado con los roles y permisos del usuario,
                    .jwtID(UUID.randomUUID().toString()) // ID único del token
                    .build();

            // Firmar el token con la clave privada
            JWSSigner signer = new RSASSASigner(privateKey);
            SignedJWT signedJWT = new SignedJWT(
                    new com.nimbusds.jose.JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaKey.getKeyID()).build(),
                    claimsSet);
            signedJWT.sign(signer);

            //return signedJWT.serialize(); // Retorna el JWT como String
            return signedJWT;
        } catch (JOSEException e) {
            log.error("Error to get the private key",e);
            throw new RuntimeException(e);
        }

    }
}
