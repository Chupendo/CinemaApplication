package com.tokioschool.filmapp.services.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Interfaz para el servicio de gestión de tokens JWT.
 *
 * Proporciona metodos para generar y decodificar tokens JWT, utilizados para la autenticación
 * y autorización en la aplicación.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public interface JwtService {

    /**
     * Genera un token JWT para un usuario autenticado.
     *
     * Este metodo utiliza los detalles del usuario proporcionados para crear un token JWT
     * que incluye información como el nombre de usuario y las autoridades.
     *
     * @param userDetails Detalles del usuario autenticado.
     * @return Un objeto {@link Jwt} que representa el token generado.
     */
    Jwt generateJwt(UserDetails userDetails);

    /**
     * Decodifica y verifica un token JWT.
     *
     * Este metodo valida el token proporcionado y devuelve su contenido si es válido.
     *
     * @param token El token JWT a decodificar.
     * @return Un objeto {@link Jwt} que representa el contenido del token decodificado.
     */
    Jwt decodeToken(String token);

}