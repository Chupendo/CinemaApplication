package com.tokioschool.filmapp.services.auth;

import com.tokioschool.filmapp.dto.auth.AuthenticatedMeResponseDto;
import com.tokioschool.filmapp.dto.auth.AuthenticationRequestDto;
import com.tokioschool.filmapp.dto.auth.AuthenticationResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.tuple.Pair;

import java.text.ParseException;

/**
 * Interfaz para el servicio de autenticación.
 *
 * Define los métodos necesarios para autenticar usuarios, obtener información del usuario autenticado
 * y gestionar tokens JWT.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public interface AuthenticationService {

    /**
     * Autentica a un usuario en el sistema utilizando sus credenciales.
     *
     * @param authenticationResponseDTO Objeto que contiene el nombre de usuario y la contraseña.
     * @return Un objeto {@link AuthenticationResponseDto} que contiene el token de acceso y su tiempo de expiración.
     * @throws ParseException Si ocurre un error al analizar los datos de autenticación.
     */
    AuthenticationResponseDto authenticate(AuthenticationRequestDto authenticationResponseDTO) throws ParseException;

    /**
     * Obtiene información del usuario autenticado que realizó la solicitud.
     *
     * @return Un objeto {@link AuthenticatedMeResponseDto} que contiene el nombre de usuario,
     * roles, autoridades y alcances del usuario autenticado.
     */
    AuthenticatedMeResponseDto getAuthenticated();

    /**
     * Obtiene el token JWT y su tiempo de expiración de la solicitud HTTP.
     *
     * @param request La solicitud HTTP que contiene el encabezado de autorización.
     * @return Un par que contiene el token JWT y su tiempo de expiración en segundos, o {@code null} si no se cumplen las condiciones.
     */
    Pair<String, Long> getTokenAndExpiredAt(HttpServletRequest request);
}