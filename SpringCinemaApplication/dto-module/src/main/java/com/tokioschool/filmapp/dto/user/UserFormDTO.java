package com.tokioschool.filmapp.dto.user;

import com.tokioschool.filmapp.enums.RoleEnum;
import com.tokioschool.filmapp.validators.anotations.EnumValid;
import com.tokioschool.filmapp.validators.anotations.PasswordBis;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
@PasswordBis
public class UserFormDTO {
    private String id;
    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$",message = "Password invalid")
    private String password;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$",message = "Password invalid")
    private String passwordBis;

    @NotBlank
    private String username;

    @Email
    private String email;

    @Size(min=36,max = 36)
    private String image;

    @NonNull
    @Past
    private LocalDate birthDate;

    @EnumValid(target = RoleEnum.class,required = true,message = "Role don't allow")
    private List<String> role;

    private boolean updatePassword = false;
    private LocalDateTime created;
}
