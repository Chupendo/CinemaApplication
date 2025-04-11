package com.tokioschool.filmapp.services.jwt.impl;

import com.tokioschool.filmapp.services.jwt.JwtService;
import com.tokioschool.jwt.properties.JwtProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Implementación del servicio JWT.
 *
 * Proporciona metodos para generar y decodificar tokens JWT utilizando las configuraciones
 * definidas en la aplicación.
 *
 * Anotaciones:
 * - {@link Service}: Marca esta clase como un componente de servicio de Spring.
 * - {@link RequiredArgsConstructor}: Genera un constructor con los argumentos requeridos para los campos finales.
 * - {@link Primary}: Indica que esta implementación es la principal para el servicio {@link JwtService}.
 *
 * Dependencias:
 * - {@link JwtProperty}: Propiedades de configuración para los tokens JWT.
 * - {@link JwtEncoder}: Componente para codificar tokens JWT.
 * - {@link JwtDecoder}: Componente para decodificar tokens JWT.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Service("jwtServiceImpl")
@RequiredArgsConstructor
@Primary
public class JwtServiceImpl implements JwtService {

    /**
     * Propiedades de configuración para los tokens JWT.
     */
    private final JwtProperty jwtConfigurationProperty;

    /**
     * Componente para codificar tokens JWT.
     */
    private final JwtEncoder jwtEncoder;

    /**
     * Componente para decodificar tokens JWT.
     */
    private final JwtDecoder jwtDecoder;

    /**
     * Genera un token JWT para un usuario autenticado.
     *
     * Este metodo crea un token JWT utilizando las credenciales del usuario y las configuraciones
     * definidas, incluyendo el algoritmo de firma, el tiempo de expiración y las autoridades del usuario.
     *
     * @param userDetails Detalles del usuario autenticado.
     * @return Un objeto {@link Jwt} que representa el token generado.
     */
    @Override
    public Jwt generateJwt(UserDetails userDetails) {
        // Header: Define el algoritmo de firma y el tipo de token.
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();

        // Claims (payload): Define el contenido del token, como el sujeto, la expiración y las autoridades.
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(userDetails.getUsername())
                .expiresAt(Instant.now().plusMillis(jwtConfigurationProperty.expiration().toMillis()))
                .claim("authorities",
                        userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();

        // Firma: Combina el encabezado y las reclamaciones para crear los parámetros del token.
        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(jwsHeader, jwtClaimsSet);

        // Codifica el token y lo devuelve.
        return jwtEncoder.encode(jwtEncoderParameters);
    }

    /**
     * Decodifica y verifica un token JWT.
     *
     * Este metodo valida el token proporcionado y devuelve su contenido si es válido.
     *
     * @param token El token JWT a decodificar.
     * @return Un objeto {@link Jwt} que representa el contenido del token decodificado.
     */
    public Jwt decodeToken(String token) {
        return jwtDecoder.decode(token);
    }
}