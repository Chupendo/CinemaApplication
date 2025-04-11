package com.tokioschool.ratingapp.services.jwt;

import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Interfaz para el servicio de manejo de tokens JWT.
 *
 * Proporciona metodos para generar, validar y extraer información de tokens JWT.
 * Este servicio es utilizado para la autenticación y autorización en la aplicación.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public interface JwtService {

    /**
     * Genera un token JWT firmado.
     *
     * Este metodo crea un token JWT con claims personalizados y lo firma
     * utilizando una clave privada.
     *
     * @param userDetails Detalles del usuario autenticado.
     * @return Un objeto `SignedJWT` que representa el token firmado.
     */
    SignedJWT generateSignedJWT(UserDetails userDetails);

    /**
     * Valida un token JWT.
     *
     * Este metodo verifica la firma del token y valida sus claims, como la expiración.
     *
     * @param token El token JWT a validar.
     * @return `true` si el token es válido, `false` en caso contrario.
     */
    boolean validateToken(String token);

    /**
     * Extrae el nombre de usuario de un token JWT.
     *
     * Este metodo analiza el token JWT y extrae el nombre de usuario contenido en él.
     *
     * @param token El token JWT del cual extraer el nombre de usuario.
     * @return El nombre de usuario contenido en el token.
     */
    String getUsernameFromToken(String token);
}