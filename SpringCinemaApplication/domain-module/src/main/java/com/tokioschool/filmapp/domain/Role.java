package com.tokioschool.filmapp.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

/**
 * Representa un rol en la aplicación.
 *
 * Esta clase define la entidad `Role` que se mapea a la tabla `roles` en la base de datos.
 * Incluye información como el nombre del rol, las autoridades asociadas y los alcances relacionados.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "roles") // Define el nombre de la tabla en la base de datos
public class Role {

    /**
     * Identificador único del rol.
     * Se genera automáticamente utilizando la estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del rol.
     * Este campo no puede ser nulo y debe ser único.
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Conjunto de autoridades asociadas al rol.
     * Este campo es una relación muchos a muchos con la entidad {@link Authority}.
     * Se utiliza una tabla intermedia `roles_authorities` para gestionar la relación.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "roles_authorities",
            joinColumns = {@JoinColumn(name = "ROLE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_ID")}
    )
    private Set<Authority> authorities;

    /**
     * Conjunto de alcances asociados al rol.
     * Este campo es una relación muchos a muchos con la entidad {@link Scope}.
     * Se utiliza una tabla intermedia `roles_scopes` para gestionar la relación.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "roles_scopes",
            joinColumns = {@JoinColumn(name = "ROLE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "SCOPE_ID")}
    )
    private Set<Scope> scopes;
}