package com.tokioschool.filmapp.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * Representa una autoridad en la aplicación.
 *
 * Esta clase define la entidad `Authority` que se mapea a la tabla `authorities` en la base de datos.
 * Incluye información básica como el identificador único y el nombre de la autoridad.
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
@Table(name = "authorities") // Define el nombre de la tabla en la base de datos
public class Authority {

    /**
     * Identificador único de la autoridad.
     * Se genera automáticamente utilizando la estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de la autoridad.
     * Este campo no puede ser nulo.
     */
    @Column(nullable = false)
    private String name;

}