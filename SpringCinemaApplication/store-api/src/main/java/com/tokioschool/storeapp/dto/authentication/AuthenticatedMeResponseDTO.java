package com.tokioschool.storeapp.dto.authentication;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * DTO que representa la respuesta de autenticación del usuario autenticado.
 *
 * Esta clase contiene información sobre el usuario autenticado, incluyendo su nombre de usuario,
 * las autoridades asignadas y los roles asociados.
 *
 * Notas:
 * - Es inmutable gracias a las anotaciones de Lombok (@Value).
 * - Se utiliza la anotación @Jacksonized para facilitar la serialización/deserialización con Jackson.
 * - Se construye utilizando el patrón Builder.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Value
@Jacksonized
@Builder
public class AuthenticatedMeResponseDTO {

    /**
     * Nombre de usuario del usuario autenticado.
     */
    String username;

    /**
     * Lista de autoridades asignadas al usuario.
     */
    List<String> authorities;

    /**
     * Lista de roles asociados al usuario.
     */
    List<String> roles;
}