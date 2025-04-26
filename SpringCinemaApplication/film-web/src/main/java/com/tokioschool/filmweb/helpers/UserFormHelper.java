package com.tokioschool.filmweb.helpers;

import com.tokioschool.filmapp.dto.user.UserDto;
import com.tokioschool.filmapp.enums.RoleEnum;
import org.springframework.lang.NonNull;

import java.util.Objects;

public class UserFormHelper {

    public static boolean isOperationEditAnUserAllowed(@NonNull  UserDto userToEdit,@NonNull UserDto userAuthenticated){
        return !isAdmin(userToEdit) || ( isAdmin(userToEdit) && Objects.nonNull( userToEdit.getId() ) && userToEdit.getId().equals(userAuthenticated.getId()));
    }

    public static boolean isAdmin(@NonNull UserDto user) {
        return user.getRoles() != null && user.getRoles().stream()
                .anyMatch(role -> RoleEnum.ADMIN.toString().equalsIgnoreCase(role.getName()));
    }
}
