package com.tokioschool.ratingapp.dtos;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RatingResponseFilmDto(BigDecimal score,
                                    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
                                    LocalDateTime localDateTime) {
}
