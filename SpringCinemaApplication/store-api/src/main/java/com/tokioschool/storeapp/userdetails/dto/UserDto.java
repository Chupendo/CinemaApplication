package com.tokioschool.storeapp.userdetails.dto;

import java.util.List;

/**
 * Representa un usuario definido en las propiedades del sistema.
 *
 * Esta clase utiliza un record para almacenar información básica de un usuario,
 * incluyendo su nombre de usuario, contraseña, autoridades y roles.
 *
 * @param username El nombre de usuario del usuario.
 * @param password La contraseña del usuario.
 * @param authorities Una lista de autoridades (privilegios) asignadas al usuario.
 * @param roles Una lista de roles asignados al usuario.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public record UserDto(String username, String password, List<String> authorities, List<String> roles) {}