package com.tokioschool.filmapp.domain;

import com.tokioschool.filmapp.enums.TYPE_ARTIST;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "artists")
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(name = "TYPE_ARTIST",nullable = false)
    @Enumerated(EnumType.STRING)
    private TYPE_ARTIST typeArtist;

}
