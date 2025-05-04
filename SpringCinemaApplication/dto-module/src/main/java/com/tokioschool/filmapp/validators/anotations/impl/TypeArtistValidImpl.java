package com.tokioschool.filmapp.validators.anotations.impl;

import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.validators.anotations.TypeArtistValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Implementación de la interfaz ConstraintValidator para validar el tipo de un artista.
 *
 * Esta clase verifica que el tipo de artista especificado en un objeto {@link ArtistDto}
 * coincida con el tipo definido en la anotación {@link TypeArtistValid}.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class TypeArtistValidImpl implements ConstraintValidator<TypeArtistValid, ArtistDto> {
    private String entry;

    /**
     * Inicializa el validador con el tipo de artista objetivo definido en la anotación {@link TypeArtistValid}.
     *
     * @param constraintAnnotation La anotación {@link TypeArtistValid} que contiene la configuración.
     */
    @Override
    public void initialize(TypeArtistValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        entry = constraintAnnotation.target().name();
    }

    /**
     * Valida un objeto {@link ArtistDto} verificando que el tipo de artista coincida con el tipo objetivo.
     *
     * @param artistDto                El objeto {@link ArtistDto} a validar.
     * @param constraintValidatorContext El contexto de validación.
     * @return true si el tipo de artista coincide con el tipo objetivo, false en caso contrario.
     */
    @Override
    public boolean isValid(ArtistDto artistDto, ConstraintValidatorContext constraintValidatorContext) {
        return artistDto != null && artistDto.getTypeArtist() != null && artistDto.getTypeArtist().equalsIgnoreCase(entry);
    }
}