package com.tokioschool.filmapp.domain;

import com.tokioschool.filmapp.enums.TYPE_ARTIST;
import jakarta.persistence.*;
import lombok.*;

/**
 * Representa un artista en la aplicación de películas.
 *
 * Esta clase define la entidad `Artist` que se mapea a la tabla `artists` en la base de datos.
 * Incluye información básica como el nombre, apellido y tipo de artista.
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
@Table(name = "artists") // Define el nombre de la tabla en la base de datos
public class Artist {

    /**
     * Identificador único del artista.
     * Se genera automáticamente utilizando la estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del artista.
     * Este campo no puede ser nulo.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Apellido del artista.
     * Este campo no puede ser nulo.
     */
    @Column(nullable = false)
    private String surname;

    /**
     * Tipo de artista.
     * Este campo almacena un valor del enumerado \{@link TYPE_ARTIST\} y no puede ser nulo.
     * Se guarda como una cadena en la base de datos.
     */
    @Column(name = "TYPE_ARTIST", nullable = false)
    @Enumerated(EnumType.STRING)
    private TYPE_ARTIST typeArtist;

}