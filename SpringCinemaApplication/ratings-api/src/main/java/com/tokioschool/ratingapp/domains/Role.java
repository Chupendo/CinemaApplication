package com.tokioschool.ratingapp.domains;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="roles_authorities",
            joinColumns =  {@JoinColumn(name="ROLE_ID")},
            inverseJoinColumns = { @JoinColumn(name="AUTHORITY_ID")})
    private Set<Authority> authorities;
}
