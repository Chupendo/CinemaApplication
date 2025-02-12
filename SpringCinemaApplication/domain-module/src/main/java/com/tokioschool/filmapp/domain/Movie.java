package com.tokioschool.filmapp.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

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

    @Column(name="resource_id",unique = true)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID image;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="movies_artists",
            joinColumns =  {@JoinColumn(name="movie_id")},
            inverseJoinColumns = { @JoinColumn(name="artist_id")})
    private List<Artist> artists;
}
