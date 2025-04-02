package com.tokioschool.ratingapp.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record RequestRatingFilmDto(Long id,@NotNull @Positive Long userId, @NotNull @Positive Long filmId,@NotNull @PositiveOrZero @Max(100) BigDecimal score) {
}
