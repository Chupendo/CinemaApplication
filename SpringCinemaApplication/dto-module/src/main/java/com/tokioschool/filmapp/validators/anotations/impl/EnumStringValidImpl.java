package com.tokioschool.filmapp.validators.anotations.impl;

import com.tokioschool.filmapp.validators.anotations.EnumListValid;
import com.tokioschool.filmapp.validators.anotations.EnumValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class EnumStringValidImpl implements ConstraintValidator<EnumValid,String> {
    private List<String> entries;
    private boolean required;

    @Override
    public void initialize(EnumValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        // initialize, get value of annotation of property target
        final Enum<?>[] constrainsEnum = constraintAnnotation.target().getEnumConstants();
        this.entries = Arrays.stream(constrainsEnum)
                .map(Enum::toString)
                .map(String::toUpperCase)
                .toList();

        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String source, ConstraintValidatorContext constraintValidatorContext) {
        // Validation
        String trimmed = Optional.ofNullable(source)
                .map(StringUtils::stripToNull)    // Trim and convert blanks to null
                .filter(Objects::nonNull)         // Filter out null values
                .map(String::toUpperCase)         // Convert to uppercase
                .orElseGet(() -> null);    // Collect as List


        // comprueba que el valor este dentro del enum a validar
        boolean isValid = this.entries.contains(trimmed);

        if( this.required ){
            // es valido
            return isValid;
        }else {
            // no es valido
            return trimmed == null || isValid;
        }
    }
}
