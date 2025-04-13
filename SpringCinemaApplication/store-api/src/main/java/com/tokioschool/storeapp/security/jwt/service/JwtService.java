package com.tokioschool.storeapp.security.jwt.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Interfaz para el servicio de generación de tokens JWT.
 *
 * Esta interfaz define el contrato para la generación de tokens JWT
 * basados en los detalles del usuario proporcionados.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public interface JwtService {

    /**
     * Genera un token JWT para un usuario.
     *
     * Este metodo crea un token JWT que incluye información relevante del usuario,
     * como su nombre de usuario y roles/autoridades.
     *
     * @param userDetails Los detalles del usuario para el cual se generará el token.
     * @return Una instancia de `Jwt` que representa el token generado.
     */
    Jwt generateToken(UserDetails userDetails);
}