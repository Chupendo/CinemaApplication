package com.tokioschool.storeapp.userdetails.service.impl;

import com.tokioschool.storeapp.userdetails.dto.UserDto;
import com.tokioschool.storeapp.userdetails.properties.StoreUserConfigurationProperty;
import com.tokioschool.storeapp.userdetails.service.StoreUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementación del servicio de usuarios de la tienda.
 *
 * Esta clase proporciona la lógica para gestionar los usuarios definidos en las propiedades
 * de configuración de la aplicación. Permite buscar usuarios en memoria por su nombre de usuario.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class StoreUserServiceImpl implements StoreUserService {

    private final StoreUserConfigurationProperty storeUserConfigurationProperty;

    /**
     * Busca un usuario cargado en memoria dado su nombre de usuario.
     *
     * Este metodo busca en la lista de usuarios definida en las propiedades de configuración
     * y devuelve el usuario correspondiente si existe.
     *
     * @param username El nombre de usuario utilizado para buscar al usuario en el sistema.
     * @return Un objeto `UserDto` que representa al usuario encontrado.
     * @throws UsernameNotFoundException Si no se encuentra un usuario con el nombre de usuario proporcionado
     *                                   o si la lista de usuarios está vacía.
     */
    public UserDto findByUserName(String username) throws UsernameNotFoundException {
        return Optional.ofNullable(storeUserConfigurationProperty.users())
                .orElseThrow(() -> new UsernameNotFoundException("User list is empty"))
                .stream()
                .filter(userDto -> userDto.username().equalsIgnoreCase(username))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User don't found"));
    }
}