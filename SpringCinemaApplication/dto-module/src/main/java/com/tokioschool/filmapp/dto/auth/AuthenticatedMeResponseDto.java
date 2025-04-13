package com.tokioschool.filmapp.dto.auth;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Clase DTO (Data Transfer Object) para representar la respuesta de un usuario autenticado.
 *
 * Esta clase se utiliza para transferir datos relacionados con la autenticación
 * de un usuario, incluyendo su nombre de usuario, autoridades, roles y alcances.
 * Es inmutable y utiliza la anotación `@Value` de Lombok.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Value
@Jacksonized
@Builder
public class AuthenticatedMeResponseDto {

    /**
     * Nombre de usuario del usuario autenticado.
     */
    String username;

    /**
     * Lista de autoridades asignadas al usuario.
     */
    List<String> authorities;

    /**
     * Lista de roles asignados al usuario.
     */
    List<String> roles;

    /**
     * Lista de alcances (scopes) asignados al usuario.
     */
    List<String> scopes;
}