package com.tokioschool.storeapp.security.jwt.service.impl;

import com.tokioschool.storeapp.security.jwt.properties.JwtConfigurationProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Implementación del servicio para la generación de tokens JWT.
 *
 * Esta clase proporciona la lógica para generar tokens JWT utilizando las propiedades
 * configuradas en `JwtConfigurationProperty` y el codificador `NimbusJwtEncoder`.
 *
 * Notas:
 * - Utiliza el algoritmo HMAC-SHA256 para firmar los tokens.
 * - Los tokens generados incluyen información del usuario y sus roles/autoridades.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements com.tokioschool.storeapp.security.jwt.service.JwtService {

    private final JwtConfigurationProperty jwtConfigurationProperty;
    private final NimbusJwtEncoder nimbusJwtEncoder;

    /**
     * Genera un token JWT para un usuario.
     *
     * Este metodo crea un token JWT que incluye el nombre de usuario como sujeto,
     * una fecha de expiración basada en las propiedades configuradas, y las autoridades
     * del usuario como un reclamo adicional.
     *
     * @param userDetails Detalles del usuario para el cual se generará el token.
     * @return Una instancia de `Jwt` que representa el token generado.
     */
    public Jwt generateToken(UserDetails userDetails) {
        // Header: Define el algoritmo de firma y el tipo de token (JWT)
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();

        // Claims (payload): Define el sujeto, la fecha de expiración y las autoridades del usuario
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(userDetails.getUsername())
                .expiresAt(Instant.now().plusMillis(jwtConfigurationProperty.expiration().toMillis()))
                .claim("authorities",
                        userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();

        // Firma: Combina el encabezado y los reclamos para crear los parámetros del token
        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(jwsHeader, jwtClaimsSet);

        // Codifica y genera el token JWT
        return nimbusJwtEncoder.encode(jwtEncoderParameters);
    }
}