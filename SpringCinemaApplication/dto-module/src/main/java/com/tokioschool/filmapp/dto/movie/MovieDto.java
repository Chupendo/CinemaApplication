package com.tokioschool.filmapp.dto.movie;

import com.tokioschool.filmapp.dto.artist.ArtistDto;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
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

    private ArtistDto managerDtoId;

    private List<ArtistDto> artistDtos;
}
