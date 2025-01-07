package com.tokioschool.filmapp.dto.artist;

import com.tokioschool.filmapp.enums.TYPE_ARTIS_DTO;
import com.tokioschool.filmapp.validators.anotations.EnumValid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArtistDto {

    private Long id;
    @NotNull
    private String name;

    @NotNull
    private String surname;

    @EnumValid(target = TYPE_ARTIS_DTO.class,required = true)
    private String typeArtist;
}
