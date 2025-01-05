package com.tokioschool.filmapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class UserFormDTO {
    private String id;
    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$",message = "Password invalid")
    private String password;

    @NotBlank
    private String username;

    @Email
    private String email;

    @NonNull
    @Past
    private LocalDate birthDate;
}
