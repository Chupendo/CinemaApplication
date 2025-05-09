package com.tokioschool.filmapp.domain;

import com.tokioschool.filmapp.tsId.TSId;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Representa un usuario en la aplicación.
 *
 * Esta clase define la entidad `User` que se mapea a la tabla `users` en la base de datos.
 * Incluye información como el nombre, apellidos, correo electrónico, credenciales,
 * fechas de creación y último inicio de sesión, imagen asociada y roles asignados.
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
@Table(name = "users") // Define el nombre de la tabla en la base de datos
public class User {

    /**
     * Identificador único del usuario.
     * Este campo utiliza una anotación personalizada `@TSId`.
     */
    @Id
    @TSId
    private String id;

    /**
     * Nombre del usuario.
     * Este campo no puede ser nulo.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Apellidos del usuario.
     * Este campo no puede ser nulo.
     */
    @Column(nullable = false)
    private String surname;

    /**
     * Fecha de nacimiento del usuario.
     */
    @Column(name = "date_of_birth")
    private LocalDate birthDate;

    /**
     * Correo electrónico del usuario.
     * Este campo no puede ser nulo y debe ser único.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Nombre de usuario.
     */
    @Column
    private String username;

    /**
     * Contraseña del usuario.
     * Este campo no puede ser nulo.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Confirmación de la contraseña del usuario.
     * Este campo no puede ser nulo.
     */
    @Column(nullable = false, name = "password_bis")
    private String passwordBis;

    /**
     * Marca de tiempo de creación del usuario.
     * Se establece automáticamente al momento de la creación.
     */
    @Column(name = "create_at")
    @CurrentTimestamp
    private LocalDateTime created;

    /**
     * Marca de tiempo del último inicio de sesión del usuario.
     */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    /**
     * Identificador único del recurso de imagen asociado al usuario.
     * Se almacena como un UUID en la base de datos.
     */
    @Column(name = "resource_id", unique = true)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID image;

    /**
     * Conjunto de roles asignados al usuario.
     * Este campo es una relación muchos a muchos con la entidad {@link Role}.
     * Se utiliza una tabla intermedia `users_roles` para gestionar la relación.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_roles",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private Set<Role> roles;

    @PrePersist
    public void ensureId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString().substring(0, 13);
        }
    }

}