package com.tokioschool.filmapp.dto.user;

import com.tokioschool.filmapp.enums.RoleEnum;
import com.tokioschool.filmapp.validators.anotations.EnumValid;
import com.tokioschool.filmapp.validators.anotations.PasswordBis;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

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

    @NonNull
    @Past
    private LocalDate birthDate;

    @EnumValid(target = RoleEnum.class,required = true,message = "Role don't allow")
    private String role;
}
