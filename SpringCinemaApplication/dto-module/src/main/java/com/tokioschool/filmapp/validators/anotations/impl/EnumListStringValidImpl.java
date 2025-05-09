package com.tokioschool.filmapp.validators.anotations.impl;

import com.tokioschool.filmapp.validators.anotations.EnumListValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Implementación de la interfaz ConstraintValidator para validar listas de cadenas
 * contra los valores de un enum especificado en la anotación {@link EnumListValid}.
 *
 * Esta clase realiza la validación de listas de cadenas, asegurándose de que los valores
 * pertenezcan al enum objetivo y cumplan con los requisitos de obligatoriedad.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class EnumListStringValidImpl implements ConstraintValidator<EnumListValid,List<String>> {
    private List<String> entries;
    private boolean required;

    @Override
    public void initialize(EnumListValid constraintAnnotation) {
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
        List<String> trimmedList = Optional.ofNullable(sources)
                .orElse(Collections.emptyList()) // Handle null safely
                .stream()
                .map(StringUtils::stripToNull)    // Trim and convert blanks to null
                .filter(Objects::nonNull)         // Filter out null values
                .map(String::toUpperCase)         // Convert to uppercase
                .toList();    // Collect as List

        if(!this.required && trimmedList.isEmpty()){ // no valida, por lo que es true
            return true;
        }

        // comprueba que el valor este dentro del enum a validar
        int count = 0;
        for (String strValue : trimmedList) {
            if(this.entries.contains(strValue)){
                count++;
            }
        }

        if( this.required && !trimmedList.isEmpty() && count == sources.size() ){
            // es valido
            return true;
        }else {
            if (this.required && !trimmedList.isEmpty() && count != sources.size()) {
                // no es valido
                return false;
            } else {
                // es valido
                return !this.required && count == sources.size();
            }

        }
    }
}