package com.tokioschool.storeapp.dto.authentication;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * DTO que representa la solicitud de autenticación de un usuario.
 *
 * Esta clase contiene las credenciales necesarias para autenticar a un usuario,
 * como el nombre de usuario y la contraseña.
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
public class AuthenticationRequestDTO {

    /**
     * Nombre de usuario del usuario que desea autenticarse.
     */
    String username;

    /**
     * Contraseña del usuario que desea autenticarse.
     */
    String password;
}