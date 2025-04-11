package com.tokioschool.filmapp.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

/**
 * Representa una película en la aplicación.
 *
 * Esta clase define la entidad `Movie` que se mapea a la tabla `movies` en la base de datos.
 * Incluye información como el título, año de lanzamiento, gerente, imagen y artistas asociados.
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
@Table(name = "movies") // Define el nombre de la tabla en la base de datos
public class Movie {

    /**
     * Identificador único de la película.
     * Se genera automáticamente utilizando la estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Título de la película.
     */
    private String title;

    /**
     * Año de lanzamiento de la película.
     */
    @Column(name = "release_year")
    private Integer releaseYear;

    /**
     * Gerente de la película.
     * Este campo es una relación uno a uno con la entidad \{@link Artist\}.
     * No puede ser nulo.
     */
    @JoinColumn(name = "manager_id", nullable = false)
    @OneToOne
    private Artist manager;

    /**
     * Identificador único del recurso de imagen asociado a la película.
     * Se almacena como un UUID en la base de datos.
     */
    @Column(name = "resource_id", unique = true)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID image;

    /**
     * Lista de artistas asociados a la película.
     * Este campo es una relación muchos a muchos con la entidad \{@link Artist\}.
     * Se utiliza una tabla intermedia `movies_artists` para gestionar la relación.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movies_artists",
            joinColumns = {@JoinColumn(name = "movie_id")},
            inverseJoinColumns = {@JoinColumn(name = "artist_id")}
    )
    private List<Artist> artists;
}