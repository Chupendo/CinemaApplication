package com.tokioschool.filmapp.validators.anotations.impl;

import com.tokioschool.filmapp.validators.anotations.EnumValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class EnumValidImpl implements ConstraintValidator<EnumValid,List<String>> {
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
    public boolean isValid(List<String> sources, ConstraintValidatorContext constraintValidatorContext) {
        // Validation
        final List<String> trimmedList = sources.stream()
                .map(StringUtils::stripToNull)
                .map(String::toUpperCase)
                .toList();

        if(!this.required){ // no valida, por lo que es true
            return true;
        }

        // comprueba que el valor este dentro del enum a validar
        int count = 0;
        for (String strValue : trimmedList) {
            if(this.entries.contains(strValue)){
                count++;
            }
        }
        return count == sources.size();
    }
}
