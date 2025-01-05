package com.tokioschool.filmapp.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class UserDTO {
    private String id;
    private String name;
    private String surname;
    private String username;
    private String email;
    private LocalDate birthDate;
    private LocalDateTime lastLogin;
    private LocalDateTime created;
    private List<RoleDTO> roles;
}
