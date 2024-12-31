package com.tokioschool.filmapp.domain;

import com.tokioschool.filmapp.tsId.TSId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @TSId
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(name = "date_of_birth")
    private LocalDate birthDate;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false,name = "password_bis")
    private String passwordBis;

    @Column(name = "create_at")
    @CurrentTimestamp
    private LocalDateTime created;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

}
