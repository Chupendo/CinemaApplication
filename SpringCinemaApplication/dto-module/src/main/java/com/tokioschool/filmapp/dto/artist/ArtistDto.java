package com.tokioschool.filmapp.dto.artist;

import com.tokioschool.filmapp.enums.TYPE_ARTIS_DTO;
import com.tokioschool.filmapp.validators.anotations.EnumValid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase DTO (Data Transfer Object) para representar un artista.
 *
 * Esta clase se utiliza para transferir datos relacionados con un artista
 * entre diferentes capas de la aplicación.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArtistDto {

    /**
     * Identificador único del artista.
     */
    private Long id;

    /**
     * Nombre del artista.
     * Este campo es obligatorio.
     */
    @NotEmpty
    private String name;

    /**
     * Apellido del artista.
     * Este campo es obligatorio.
     */
    @NotEmpty
    private String surname;

    /**
     * Tipo de artista.
     * Este campo es validado para asegurarse de que pertenece a los valores definidos
     * en la enumeración {@link TYPE_ARTIS_DTO}.
     */
    @EnumValid(target = TYPE_ARTIS_DTO.class, required = true)
    private String typeArtist;
}