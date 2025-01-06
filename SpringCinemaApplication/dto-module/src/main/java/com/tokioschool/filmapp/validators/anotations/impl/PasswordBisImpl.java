package com.tokioschool.filmapp.validators.anotations.impl;

import com.tokioschool.filmapp.dto.user.UserFormDTO;
import com.tokioschool.filmapp.validators.anotations.PasswordBis;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Validated of password, checking that password and passwordbis are equals and matches with pattern @{@link  #PASSWORD_PATTERN}
 *
 * @author anres.rpenuela
 * @version 1.0
 */
public class PasswordBisImpl implements ConstraintValidator<PasswordBis, UserFormDTO> {
    /**
     * Length min., 8 chars.
     * min., a letter lower case (a-z).
     * min., a letter upper case  (A-Z).
     * min., a digit (0-9).
     * min., a character of the next list: @, #, $, %, ^, &, +, =.
     */
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$";

    @Override
    public void initialize(PasswordBis constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserFormDTO source, ConstraintValidatorContext constraintValidatorContext) {
        return Optional.ofNullable(source)
                .filter(user -> user.getCreated() == null || user.isUpdatePassword())
                .filter(user -> Objects.equals(user.getPassword(),user.getPasswordBis()))
                .map(UserFormDTO::getPassword)
                .map(password -> Pattern.matches(PASSWORD_PATTERN, password))
                .orElseGet(()->Boolean.FALSE);
    }
}
