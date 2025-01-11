package com.tokioschool.filmapp.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name="release_year")
    private Integer releaseYear;

    @JoinColumn(name="manager_id",nullable = false/*, referencedColumnName = "id"*/)
    @OneToOne
    //@PrimaryKeyJoinColumn
    private Artist manager;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="movies_artists",
            joinColumns =  {@JoinColumn(name="movie_id")},
            inverseJoinColumns = { @JoinColumn(name="artist_id")})
    private List<Artist> artists;
}
