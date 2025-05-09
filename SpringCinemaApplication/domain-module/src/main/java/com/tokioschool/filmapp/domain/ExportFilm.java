package com.tokioschool.filmapp.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad que representa la exportación de películas en la base de datos.
 *
 * Esta clase mapea la tabla `exported_films` y contiene información sobre
 * las exportaciones realizadas, incluyendo el ID de la película, el ID del
 * trabajo asociado y la fecha de exportación.
 *
 * Anotaciones utilizadas:
 * - `@Entity`: Marca esta clase como una entidad JPA.
 * - `@Table`: Especifica el nombre de la tabla en la base de datos.
 * - `@Id`: Indica el campo que es la clave primaria.
 * - `@GeneratedValue`: Configura la estrategia de generación de valores para la clave primaria.
 * - `@Column`: Define el mapeo de columnas en la base de datos.
 * - Anotaciones de Lombok (`@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`):
 *   Generan automáticamente métodos y constructores para la clase.
 *
 * Campos:
 * - `id`: Identificador único de la exportación.
 * - `filmId`: Identificador de la película exportada.
 * - `jobId`: Identificador del trabajo asociado a la exportación.
 * - `exportedAt`: Marca de tiempo de la exportación (establecida por la base de datos).
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
@Table(name = "exported_films") // Define el nombre de la tabla en la base de datos
public class ExportFilm {

    /**
     * Identificador único de la exportación.
     * Generado automáticamente por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador de la película exportada.
     * Este campo es obligatorio.
     */
    @OneToOne
    @JoinColumn(name = "film_id",nullable = false)  // or use @PrimaryKeyJoinColumn if IDs are the same
    private Movie movie;

    /**
     * Identificador del trabajo asociado a la exportación.
     * Este campo es obligatorio.
     */
    @Column(name = "job_id", nullable = false)
    private Long jobId;

    /**
     * Marca de tiempo de la exportación.
     * Este campo es gestionado por la base de datos y no es actualizable ni insertable desde la aplicación.
     */
    @Column(name = "exported_at", nullable = false, updatable = false, insertable = false)
    @CurrentTimestamp
    private LocalDateTime exportedAt;
}