package com.tokioschool.ratingapp.services;

import com.tokioschool.filmapp.dto.auth.AuthenticatedMeResponseDto;
import com.tokioschool.filmapp.dto.auth.AuthenticationRequestDto;
import com.tokioschool.filmapp.dto.auth.AuthenticationResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.tuple.Pair;

import java.text.ParseException;

/**
 * Interfaz para el servicio de autenticación.
 *
 * Proporciona metodos para autenticar usuarios, obtener información del usuario autenticado
 * y gestionar tokens JWT, incluyendo su tiempo de expiración.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public interface AuthenticationService {

    /**
     * Autentica un usuario en el sistema.
     *
     * Este metodo valida las credenciales del usuario y genera un token JWT
     * que incluye información sobre el usuario autenticado.
     *
     * @param authenticationResponseDTO Objeto que contiene las credenciales del usuario.
     * @return Un objeto `AuthenticationResponseDto` con el token JWT y su tiempo de expiración.
     * @throws ParseException Si ocurre un error al analizar el token JWT.
     */
    AuthenticationResponseDto authenticate(AuthenticationRequestDto authenticationResponseDTO) throws ParseException;

    /**
     * Obtiene información del usuario autenticado.
     *
     * Este metodo devuelve el nombre de usuario, roles y autoridades del usuario
     * que realizó la solicitud.
     *
     * @return Un objeto `AuthenticatedMeResponseDto` con la información del usuario autenticado.
     */
    AuthenticatedMeResponseDto getAuthenticated();

    /**
     * Obtiene el token JWT y su tiempo de expiración.
     *
     * Este metodo verifica si el token de la solicitud coincide con el token autenticado
     * y devuelve el token junto con su tiempo de expiración.
     *
     * @param request La solicitud HTTP que contiene el encabezado `Authorization`.
     * @return Un par que contiene el token JWT y su tiempo de expiración en segundos,
     *         o `null` si no se cumplen las condiciones.
     */
    Pair<String, Long> getTokenAndExpiredAt(HttpServletRequest request);
}