package com.tokioschool.filmweb.helpers;

import com.tokioschool.filmapp.dto.user.UserDto;
import com.tokioschool.filmapp.enums.RoleEnum;
import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * Clase de utilidad para operaciones relacionadas con formularios de usuarios.
 *
 * Esta clase proporciona métodos estáticos para verificar permisos y roles
 * relacionados con usuarios en el contexto de la aplicación.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class UserFormHelper {

    /**
     * Verifica si la operación de edición de un usuario está permitida.
     *
     * La edición está permitida si el usuario a editar no es administrador,
     * o si es administrador y el usuario autenticado es el mismo que se está editando.
     *
     * @param userToEdit El usuario que se desea editar.
     * @param userAuthenticated El usuario actualmente autenticado.
     * @return `true` si la operación está permitida, de lo contrario `false`.
     */
    public static boolean isOperationEditAnUserAllowed(@NonNull UserDto userToEdit, @NonNull UserDto userAuthenticated) {
        return !isAdmin(userToEdit) || (isAdmin(userToEdit) && Objects.nonNull(userToEdit.getId()) && userToEdit.getId().equals(userAuthenticated.getId()));
    }

    /**
     * Verifica si un usuario tiene el rol de administrador.
     *
     * @param user El usuario a verificar.
     * @return `true` si el usuario tiene el rol de administrador, de lo contrario `false`.
     */
    public static boolean isAdmin(@NonNull UserDto user) {
        return user.getRoles() != null && user.getRoles().stream()
                .anyMatch(role -> RoleEnum.ADMIN.toString().equalsIgnoreCase(role.getName()));
    }
}