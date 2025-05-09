package com.tokioschool.filmweb.helpers;

import com.tokioschool.filmapp.dto.user.RoleDto;
import com.tokioschool.filmapp.dto.user.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
class UserFormHelperUTest {

    @DisplayName("isOperationEditAnUserAllowed should return true when editing a non-admin user")
    @Test
    void isOperationEditAnUserAllowedShouldReturnTrueForNonAdminUser() {
        UserDto userToEdit = new UserDto();
        userToEdit.setRoles(List.of(RoleDto.builder().name("USER").build()));
        UserDto userAuthenticated = new UserDto();

        boolean result = UserFormHelper.isOperationEditAnUserAllowed(userToEdit, userAuthenticated);

        assertTrue(result);
    }

    @DisplayName("isOperationEditAnUserAllowed should return true when editing the same admin user")
    @Test
    void isOperationEditAnUserAllowedShouldReturnTrueForSameAdminUser() {
        UserDto userToEdit = new UserDto();
        userToEdit.setId("1L");
        userToEdit.setRoles(List.of(RoleDto.builder().name("ADMIN").build()));
        UserDto userAuthenticated = new UserDto();
        userAuthenticated.setId("1L");

        boolean result = UserFormHelper.isOperationEditAnUserAllowed(userToEdit, userAuthenticated);

        assertTrue(result);
    }

    @DisplayName("isOperationEditAnUserAllowed should return false when editing a different admin user")
    @Test
    void isOperationEditAnUserAllowedShouldReturnFalseForDifferentAdminUser() {
        UserDto userToEdit = new UserDto();
        userToEdit.setId("1L");
        userToEdit.setRoles(List.of(RoleDto.builder().name("ADMIN").build()));
        UserDto userAuthenticated = new UserDto();
        userAuthenticated.setId("2L");

        boolean result = UserFormHelper.isOperationEditAnUserAllowed(userToEdit, userAuthenticated);

        assertFalse(result);
    }

    @DisplayName("isAdmin should return true for a user with admin role")
    @Test
    void isAdminShouldReturnTrueForAdminRole() {
        UserDto user = new UserDto();
        user.setRoles(List.of(RoleDto.builder().name("ADMIN").build()));

        boolean result = UserFormHelper.isAdmin(user);

        assertTrue(result);
    }

    @DisplayName("isAdmin should return false for a user without admin role")
    @Test
    void isAdminShouldReturnFalseForNonAdminRole() {
        UserDto user = new UserDto();
        user.setRoles(List.of(RoleDto.builder().name("USER").build()));

        boolean result = UserFormHelper.isAdmin(user);

        assertFalse(result);
    }

    @DisplayName("isAdmin should return false for a user with null roles")
    @Test
    void isAdminShouldReturnFalseForNullRoles() {
        UserDto user = new UserDto();
        user.setRoles(null);

        boolean result = UserFormHelper.isAdmin(user);

        assertFalse(result);
    }

    @DisplayName("isAdmin should return false for a user with empty roles")
    @Test
    void isAdminShouldReturnFalseForEmptyRoles() {
        UserDto user = new UserDto();
        user.setRoles(Collections.emptyList());

        boolean result = UserFormHelper.isAdmin(user);

        assertFalse(result);
    }
}