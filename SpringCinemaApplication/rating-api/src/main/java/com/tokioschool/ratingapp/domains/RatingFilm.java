package com.tokioschool.ratingapp.domains;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "film_ratings")
public class RatingFilm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal score;

    @Column(name= "film_id",nullable = false)
    private Long filmId;

    @Column(name = "user_id",nullable = false)
    private Long userId;

    @Column(name = "create_at")
    @CurrentTimestamp
    private LocalDateTime createAt;

    @Column(name = "updated_at")
    private LocalDateTime updateAt;
}
