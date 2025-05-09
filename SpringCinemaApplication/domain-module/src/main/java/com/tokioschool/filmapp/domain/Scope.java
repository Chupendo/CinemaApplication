package com.tokioschool.filmapp.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * Representa un alcance en la aplicación.
 *
 * Esta clase define la entidad `Scope` que se mapea a la tabla `scopes` en la base de datos.
 * Incluye información básica como el identificador único y el nombre del alcance.
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
@Table(name = "scopes") // Define el nombre de la tabla en la base de datos
public class Scope {

    /**
     * Identificador único del alcance.
     * Se genera automáticamente utilizando la estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del alcance.
     * Este campo no puede ser nulo.
     */
    @Column(nullable = false)
    private String name;

}