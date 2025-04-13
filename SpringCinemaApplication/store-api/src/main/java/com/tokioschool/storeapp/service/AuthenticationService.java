package com.tokioschool.storeapp.service;

import com.tokioschool.storeapp.dto.authentication.AuthenticatedMeResponseDTO;
import com.tokioschool.storeapp.dto.authentication.AuthenticationRequestDTO;
import com.tokioschool.storeapp.dto.authentication.AuthenticationResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;

import javax.security.auth.login.LoginException;

/**
 * Interfaz para el servicio de autenticación.
 *
 * Esta interfaz define los metodos necesarios para gestionar la autenticación
 * de usuarios, incluyendo la generación de tokens, la obtención de información
 * del usuario autenticado y la validación de tokens.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public interface AuthenticationService {

    /**
     * Autentica a un usuario en el sistema.
     *
     * Este metodo valida las credenciales proporcionadas por el usuario y genera
     * un token JWT si la autenticación es exitosa.
     *
     * @param authenticationResponseDTO Objeto que contiene las credenciales del usuario (nombre de usuario y contraseña).
     * @return Un objeto `AuthenticationResponseDTO` que contiene el token JWT y su tiempo de expiración.
     * @throws BadCredentialsException Si las credenciales proporcionadas son incorrectas.
     */
    AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationResponseDTO) throws BadCredentialsException;

    /**
     * Obtiene los datos del usuario autenticado con el rol 'ADMIN'.
     *
     * Este metodo solo puede ser accedido por usuarios con el rol 'ADMIN'.
     *
     * @return Un objeto `AuthenticatedMeResponseDTO` con los datos del usuario autenticado.
     * @throws LoginException Si no hay un usuario autenticado.
     */
    @PreAuthorize("hasRole('ADMIN')")
    AuthenticatedMeResponseDTO getAuthenticated() throws LoginException;

    /**
     * Obtiene el token JWT y su tiempo de expiración desde una solicitud HTTP.
     *
     * Este metodo requiere que el usuario esté autenticado.
     *
     * @param request La solicitud HTTP que contiene el encabezado de autorización con el token JWT.
     * @return Un par (`Pair`) que contiene el valor del token y su tiempo de expiración en segundos.
     */
    @PreAuthorize("isAuthenticated()")
    Pair<String, Long> getTokenAndExpiredAt(HttpServletRequest request);
}