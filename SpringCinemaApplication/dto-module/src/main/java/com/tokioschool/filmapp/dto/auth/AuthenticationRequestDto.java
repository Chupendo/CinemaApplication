package com.tokioschool.filmapp.dto.auth;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Clase DTO (Data Transfer Object) para representar una solicitud de autenticación.
 *
 * Esta clase se utiliza para transferir los datos necesarios para autenticar
 * a un usuario, como el nombre de usuario y la contraseña.
 * Es inmutable y utiliza la anotación `@Value` de Lombok.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Value
@Jacksonized
@Builder
public class AuthenticationRequestDto {

    /**
     * Nombre de usuario del usuario que solicita autenticación.
     */
    String username;

    /**
     * Contraseña del usuario que solicita autenticación.
     */
    String password;
}