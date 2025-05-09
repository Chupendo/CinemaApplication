package com.tokioschool.filmapp.validators.anotations.impl;

import com.tokioschool.filmapp.dto.user.UserFormDto;
import com.tokioschool.filmapp.validators.anotations.PasswordBis;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Implementación de la interfaz ConstraintValidator para validar contraseñas.
 *
 * Esta clase verifica que los campos de contraseña y confirmación de contraseña (passwordBis)
 * sean iguales y que la contraseña cumpla con un patrón definido por {@link #PASSWORD_PATTERN}.
 *
 * @author anres.rpenuela
 * @version 1.0
 */
public class PasswordBisImpl implements ConstraintValidator<PasswordBis, UserFormDto> {
    /**
     * Patrón de validación para contraseñas:
     * - Longitud mínima de 8 caracteres.
     * - Al menos una letra minúscula (a-z).
     * - Al menos una letra mayúscula (A-Z).
     * - Al menos un dígito (0-9).
     * - Al menos un carácter especial de la lista: @, #, $, %, ^, &, +, =.
     */
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$";

    /**
     * Inicializa el validador con la configuración de la anotación {@link PasswordBis}.
     *
     * @param constraintAnnotation La anotación {@link PasswordBis} que contiene la configuración.
     */
    @Override
    public void initialize(PasswordBis constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * Valida un objeto {@link UserFormDto} verificando que las contraseñas coincidan
     * y que la contraseña cumpla con el patrón definido.
     *
     * @param source                   El objeto {@link UserFormDto} a validar.
     * @param context El contexto de validación.
     * @return true si la validación es exitosa, false en caso contrario.
     */
    @Override
    public boolean isValid(UserFormDto source, ConstraintValidatorContext context) {
        if(source == null) {
            return false; // Si el objeto es nulo, no se considera válido.
        }

        // Validar si se requiere la contraseña
        if (source.getCreated() == null || source.isUpdatePassword()) {
            // Verificar que las contraseñas coincidan
            if (!Objects.equals(source.getPassword(), source.getPasswordBis())) {
                context.disableDefaultConstraintViolation(); //evita que el mensaje por defecto se aplique
                context.buildConstraintViolationWithTemplate("{form.error.user.password.biss}")
                        .addPropertyNode("passwordBis")
                        .addConstraintViolation();
                return false;
            }

            // Verificar que la contraseña cumpla con el patrón
            if (!Pattern.matches(PASSWORD_PATTERN, source.getPassword())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("{form.error.user.password.pattern}")
                        .addPropertyNode("password")
                        .addConstraintViolation();
                return false;
            }
        }
        // Si todas las validaciones pasan, se considera válido
        return true;
    }
}