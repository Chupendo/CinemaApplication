package com.tokioschool.filmapp.enums;

/**
 * Enumeración que representa los roles disponibles en la aplicación.
 *
 * Esta enumeración define los diferentes roles que un usuario puede tener,
 * como ADMIN o USER, para gestionar los permisos y accesos dentro del sistema.
 *
 * Es utilizada en validaciones y asignaciones de roles a los usuarios.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public enum RoleEnum {
    /**
     * Rol de administrador con permisos elevados.
     */
    ADMIN,

    /**
     * Rol de usuario estándar con permisos básicos.
     */
    USER
}