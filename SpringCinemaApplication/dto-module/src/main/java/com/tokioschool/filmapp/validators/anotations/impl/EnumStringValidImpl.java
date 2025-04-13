package com.tokioschool.filmapp.validators.anotations.impl;

import com.tokioschool.filmapp.validators.anotations.EnumListValid;
import com.tokioschool.filmapp.validators.anotations.EnumValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Implementación de la interfaz ConstraintValidator para validar cadenas
 * contra los valores de un enum especificado en la anotación {@link EnumValid}.
 *
 * Esta clase realiza la validación de cadenas, asegurándose de que el valor
 * pertenezca al enum objetivo y cumpla con los requisitos de obligatoriedad.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class EnumStringValidImpl implements ConstraintValidator<EnumValid, String> {
    private List<String> entries;
    private boolean required;

    /**
     * Inicializa el validador con los valores del enum objetivo y la configuración
     * de obligatoriedad definida en la anotación {@link EnumValid}.
     *
     * @param constraintAnnotation La anotación {@link EnumValid} que contiene
     *                             la configuración de validación.
     */
    @Override
    public void initialize(EnumValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        // Obtiene los valores del enum objetivo y los convierte a una lista de cadenas en mayúsculas
        final Enum<?>[] constrainsEnum = constraintAnnotation.target().getEnumConstants();
        this.entries = Arrays.stream(constrainsEnum)
                .map(Enum::toString)
                .map(String::toUpperCase)
                .toList();

        this.required = constraintAnnotation.required();
    }

    /**
     * Valida una cadena contra los valores del enum objetivo.
     *
     * @param source                   La cadena a validar.
     * @param constraintValidatorContext El contexto de validación.
     * @return true si la cadena es válida, false en caso contrario.
     */
    @Override
    public boolean isValid(String source, ConstraintValidatorContext constraintValidatorContext) {
        // Maneja valores nulos y convierte la cadena a mayúsculas
        String trimmed = Optional.ofNullable(source)
                .map(StringUtils::stripToNull)    // Recorta y convierte espacios en blanco a nulos
                .filter(Objects::nonNull)         // Filtra valores nulos
                .map(String::toUpperCase)         // Convierte a mayúsculas
                .orElseGet(() -> null);

        // Comprueba que el valor esté dentro del enum objetivo
        boolean isValid = this.entries.contains(trimmed);

        if (this.required) {
            // Es válido si es obligatorio y el valor coincide
            return isValid;
        } else {
            // Es válido si no es obligatorio y el valor es nulo o coincide
            return trimmed == null || isValid;
        }
    }
}