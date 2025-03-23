package com.tokioschool.ratingapp.records;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

// todo probar validations
@Builder
public record RequestRatingFilmDto(
        @NotNull String userId,
        @NotNull Long filmId,
        @NotNull @Max(100) @Min(0) BigDecimal score) {

}
