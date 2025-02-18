package com.tokioschool.filmapp.dto.movie;

import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.enums.TYPE_ARTIS_DTO;
import com.tokioschool.filmapp.validators.anotations.TypeArtistValid;
import com.tokioschool.filmapp.validators.anotations.TypeArtistsValid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieDto {
    private Long id;

    @NotEmpty
    private String title;

    @Positive
    @Digits(integer = 4,fraction = 0)
    private Integer releaseYear;

    @TypeArtistValid(target = TYPE_ARTIS_DTO.DIRECTOR,message = "Error to validate to manager")
    private ArtistDto managerDto;

    @TypeArtistsValid(target = TYPE_ARTIS_DTO.ACTOR,message = "Error to validate to artis")
    private List<ArtistDto> artistDtos;

    @Size(min=36,max = 36)
    private String resourceId;
}
