package com.tokioschool.filmapp.dto.user;

import com.tokioschool.filmapp.enums.RoleEnum;
import com.tokioschool.filmapp.validators.anotations.EnumListValid;
import com.tokioschool.filmapp.validators.anotations.PasswordBis;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Clase DTO (Data Transfer Object) para representar el formulario de creación o actualización de un usuario.
 *
 * Esta clase se utiliza para transferir datos relacionados con el formulario de usuario,
 * incluyendo información personal, credenciales, roles y fechas importantes.
 * Utiliza validaciones para garantizar la integridad de los datos.
 * Es mutable y utiliza las anotaciones de Lombok para generar automáticamente
 * los métodos getter, setter, constructor, etc.
 *
 * La anotación personalizada `@PasswordBis` valida que los campos de contraseña coincidan.
 *
 * @author
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@PasswordBis
public class UserFormDto {

    /**
     * Identificador único del usuario.
     */
    private String id;

    /**
     * Nombre del usuario.
     * No puede estar en blanco.
     */
    @NotBlank
    private String name;

    /**
     * Apellido del usuario.
     * No puede estar en blanco.
     */
    @NotBlank
    private String surname;

    /**
     * Contraseña del usuario.
     * Debe cumplir con un patrón que garantice seguridad (mínimo 8 caracteres,
     * al menos una letra mayúscula, una minúscula, un número y un carácter especial).
     *
     *      * Verificado en {@link PasswordBis} para garantizar que coincida con la contraseña.
     */
    //@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$",     message = "{form.error.user.password.pattern}" )
    private String password;

    /**
     * Confirmación de la contraseña del usuario.
     * Debe cumplir con el mismo patrón que la contraseña.
     * Verificado en {@link PasswordBis} para garantizar que coincida con la contraseña.
     */
    //@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$",     message = "{form.error.user.password.pattern}" )
    private String passwordBis;

    /**
     * Nombre de usuario utilizado para iniciar sesión.
     * No puede estar en blanco.
     */
    @NotBlank
    private String username;

    /**
     * Dirección de correo electrónico del usuario.
     * Debe ser una dirección válida.
     */
    @Email
    private String email;

    /**
     * Identificador de la imagen asociada al usuario.
     * Debe tener exactamente 36 caracteres.
     */
    @Size(min = 36, max = 36)
    private String image;

    /**
     * Fecha de nacimiento del usuario.
     * Debe ser una fecha en el pasado.
     * No puede ser nula.
     */
    @NotNull(message = "{form.error.user.birthdate.notnull}")
    @Past(message = "{form.error.user.birthdate.past}")
    //@Builder.Default
    //@DateTimeFormat(pattern = "yyyy-MM-dd") // requerido para el formato de fecha en el formulario de tipo date
    private LocalDate birthDate;

    /**
     * Lista de roles asociados al usuario.
     * Validada para garantizar que los valores pertenezcan al enumerado RoleEnum.
     */
    @EnumListValid(target = RoleEnum.class, required = true, message = "{form.error.user.roles.notvalid}")
    private List<String> roles;

    /**
     * Indicador de si se debe actualizar la contraseña.
     * Por defecto es falso.
     */
    private boolean updatePassword = false;

    /**
     * Fecha y hora de creación del usuario.
     */
    //@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // requerido para el formato de fecha en el formulario de tipo datetime-local
    private LocalDateTime created;

    //@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // requerido para el formato de fecha en el formulario de tipo datetime-local
    private LocalDateTime lastLogin;


}