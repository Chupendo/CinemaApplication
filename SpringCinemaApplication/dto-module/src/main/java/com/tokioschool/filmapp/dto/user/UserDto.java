package com.tokioschool.filmapp.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Clase DTO (Data Transfer Object) para representar un usuario.
 *
 * Esta clase se utiliza para transferir datos relacionados con un usuario,
 * incluyendo su información personal, credenciales, roles y fechas importantes.
 * Es mutable y utiliza las anotaciones de Lombok para generar automáticamente
 * los métodos getter, setter, constructor, etc.
 *
 * @author
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    /**
     * Identificador único del usuario.
     */
    private String id;

    /**
     * Nombre del usuario.
     */
    private String name;

    /**
     * Apellido del usuario.
     */
    private String surname;

    /**
     * Nombre de usuario utilizado para iniciar sesión.
     */
    private String username;

    /**
     * Dirección de correo electrónico del usuario.
     */
    private String email;

    /**
     * Fecha de nacimiento del usuario.
     */
    private LocalDate birthDate;

    /**
     * Fecha y hora del último inicio de sesión del usuario.
     */
    private LocalDateTime lastLogin;

    /**
     * Fecha y hora de creación del usuario.
     */
    private LocalDateTime created;

    /**
     * Lista de roles asociados al usuario.
     */
    private List<RoleDto> roles;

    /**
     * Identificador del recurso asociado al usuario.
     */
    private String resourceId;
}