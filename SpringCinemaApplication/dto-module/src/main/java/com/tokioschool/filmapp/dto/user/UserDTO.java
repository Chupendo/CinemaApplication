package com.tokioschool.filmapp.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class UserDTO {
    private String id;
    private String name;
    private String surname;
    private String email;
    private LocalDate birthDate;
    private LocalDateTime lastLogin;
    private LocalDateTime created;
}
