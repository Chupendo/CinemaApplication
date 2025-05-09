package com.tokioschool.filmapp.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Clase DTO (Data Transfer Object) para representar un rol de usuario.
 *
 * Esta clase se utiliza para transferir datos relacionados con los roles
 * de usuario, incluyendo su identificador, nombre, autoridades y alcances.
 * Es mutable y utiliza las anotaciones de Lombok para generar automáticamente
 * los métodos getter, setter, constructor, etc.
 *
 * @author andres
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleDto {

    /**
     * Identificador único del rol.
     */
    private Long id;

    /**
     * Nombre del rol.
     */
    private String name;

    /**
     * Lista de autoridades asociadas al rol.
     */
    private List<String> authorities;

    /**
     * Lista de alcances (scopes) asociados al rol.
     */
    private List<String> scopes;

}