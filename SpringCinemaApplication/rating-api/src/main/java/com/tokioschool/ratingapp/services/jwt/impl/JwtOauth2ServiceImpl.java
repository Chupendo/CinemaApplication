package com.tokioschool.ratingapp.services.jwt.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.tokioschool.jwt.properties.JwtProperty;
import com.tokioschool.ratingapp.services.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Implementación del servicio JWT para OAuth2.
 *
 * Proporciona metodos para generar, validar y extraer información de tokens JWT.
 * Utiliza claves RSA para firmar y verificar los tokens.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Service("jwtOauth2Service")
@Slf4j
@RequiredArgsConstructor
public class JwtOauth2ServiceImpl implements JwtService {

    /**
     * Fuente de claves JWK utilizada para firmar y verificar tokens.
     */
    private final JWKSource<SecurityContext> jwkSource;

    /**
     * Propiedades de configuración para los tokens JWT.
     */
    private final JwtProperty jwtProperty;

    /**
     * Genera un token JWT firmado.
     *
     * Este metodo crea un token JWT con claims personalizados y lo firma
     * utilizando una clave privada obtenida de la fuente JWK.
     *
     * @param userDetails Detalles del usuario autenticado.
     * @return Un objeto `SignedJWT` que representa el token firmado.
     * @throws RuntimeException Si ocurre un error al generar el token.
     */
    @Override
    public SignedJWT generateSignedJWT(UserDetails userDetails) {
        try {
            JWKSelector jwkSelector = new JWKSelector(new JWKMatcher.Builder().build());
            JWK jwk = jwkSource.get(jwkSelector, null).getFirst();
            RSAKey rsaKey = (RSAKey) jwk;
            RSAPrivateKey privateKey = rsaKey.toRSAPrivateKey();

            // Claims (payload): a declaración o información sobre un usuario o entidad (estandar + personalizada)
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userDetails.getUsername())
                    .issuer("http://127.0.0.1:9095")
                    .issueTime(Date.from(Instant.now()))
                    .expirationTime(Date.from(Instant.now().plusMillis(jwtProperty.expiration().toMillis() + 1)))
                    .claim("authorities", userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .map(String::toUpperCase)
                            .filter(authority -> !authority.contains("SCOPE"))
                            .toList())
                    .claim("scope", userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .map(String::toUpperCase)
                            .filter(authority -> authority.contains("SCOPE"))
                            .map(authority -> authority.replace("SCOPE_", ""))
                            .toList())
                    .jwtID(UUID.randomUUID().toString())
                    .build();

            JWSSigner signer = new RSASSASigner(privateKey);
            SignedJWT signedJWT = new SignedJWT(
                    new com.nimbusds.jose.JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaKey.getKeyID()).build(),
                    claimsSet);
            signedJWT.sign(signer);

            return signedJWT;
        } catch (JOSEException e) {
            log.error("Error al obtener la clave privada", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Valida un token JWT.
     *
     * Este metodo verifica la firma del token y valida sus claims, como la expiración.
     *
     * @param token El token JWT a validar.
     * @return `true` si el token es válido, `false` en caso contrario.
     */
    @Override
    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWKSelector jwkSelector = new JWKSelector(new JWKMatcher.Builder().keyID(signedJWT.getHeader().getKeyID()).build());
            JWK jwk = jwkSource.get(jwkSelector, null).getFirst();

            if (jwk == null) {
                throw new JOSEException("No se encontró una clave JWK coincidente para el ID de clave: " + signedJWT.getHeader().getKeyID());
            }

            RSAKey rsaKey = (RSAKey) jwk;
            PublicKey publicKey = rsaKey.toRSAPublicKey();
            RSASSAVerifier verifier = new RSASSAVerifier((RSAPublicKey) publicKey);

            if (!signedJWT.verify(verifier)) {
                throw new JOSEException("La verificación de la firma del JWT falló.");
            }

            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expirationTime == null || expirationTime.before(new Date())) {
                throw new JOSEException("El token JWT ha expirado.");
            }

            return true;
        } catch (ParseException | JOSEException e) {
            log.error("La validación del token JWT falló", e);
            return false;
        }
    }

    /**
     * Extrae el nombre de usuario de un token JWT.
     *
     * @param token El token JWT del cual extraer el nombre de usuario.
     * @return El nombre de usuario contenido en el token.
     * @throws RuntimeException Si ocurre un error al analizar el token.
     */
    public String getUsernameFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            return claims.getSubject();
        } catch (ParseException e) {
            log.error("Error al extraer el nombre de usuario del token JWT", e);
            throw new RuntimeException("Error al extraer el nombre de usuario del token");
        }
    }
}