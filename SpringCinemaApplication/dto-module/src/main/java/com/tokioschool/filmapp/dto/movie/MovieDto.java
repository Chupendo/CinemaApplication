package com.tokioschool.filmapp.dto.movie;

import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.dto.user.UserDto;
import com.tokioschool.filmapp.enums.TYPE_ARTIS_DTO;
import com.tokioschool.filmapp.validators.anotations.TypeArtistValid;
import com.tokioschool.filmapp.validators.anotations.TypeArtistsValid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Clase DTO (Data Transfer Object) para representar una película.
 *
 * Esta clase se utiliza para transferir datos relacionados con una película,
 * incluyendo su título, año de lanzamiento, director, actores y otros detalles.
 * Utiliza validaciones para garantizar la integridad de los datos.
 *
 * Es mutable y utiliza las anotaciones de Lombok para generar automáticamente
 * los métodos getter, setter, constructor, etc.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieDto {

    /**
     * Identificador único de la película.
     */
    private Long id;

    /**
     * Título de la película.
     * Debe ser un valor no vacío.
     */
    @NotEmpty
    private String title;

    /**
     * Año de lanzamiento de la película.
     * Debe ser un valor positivo y contener un máximo de 4 dígitos.
     */
    @Positive
    @Digits(integer = 4, fraction = 0)
    private Integer releaseYear;

    /**
     * Director de la película.
     * Validado para garantizar que sea del tipo DIRECTOR.
     */
    @TypeArtistValid(target = TYPE_ARTIS_DTO.DIRECTOR, message = "Error to validate to manager")
    private ArtistDto managerDto;

    /**
     * Lista de actores de la película.
     * Validada para garantizar que todos los elementos sean del tipo ACTOR.
     */
    @TypeArtistsValid(target = TYPE_ARTIS_DTO.ACTOR, message = "Error to validate to artis")
    private List<ArtistDto> artistDtos;

    /**
     * Identificador del recurso asociado a la película.
     * Debe tener exactamente 36 caracteres.
     */
    @Size(min = 36, max = 36)
    private String resourceId;

    private UserDto createUser;
}