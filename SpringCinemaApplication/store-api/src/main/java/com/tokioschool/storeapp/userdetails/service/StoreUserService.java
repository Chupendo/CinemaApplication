package com.tokioschool.storeapp.userdetails.service;

import com.tokioschool.storeapp.userdetails.dto.UserDto;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Servicio para la gestión de usuarios de la tienda.
 *
 * Esta interfaz define los métodos necesarios para interactuar con los usuarios
 * configurados en el sistema, como la búsqueda de usuarios por su nombre de usuario.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public interface StoreUserService {

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * Este metodo permite obtener un usuario definido en el sistema utilizando
     * su nombre de usuario. Si no se encuentra el usuario, se lanza una excepción.
     *
     * @param username El nombre de usuario utilizado para buscar al usuario.
     * @return Un objeto `UserDto` que representa al usuario encontrado.
     * @throws UsernameNotFoundException Si no se encuentra un usuario con el nombre de usuario proporcionado.
     */
    UserDto findByUserName(String username) throws UsernameNotFoundException;
}