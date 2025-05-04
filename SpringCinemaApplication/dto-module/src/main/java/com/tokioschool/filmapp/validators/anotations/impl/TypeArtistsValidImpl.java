package com.tokioschool.filmapp.validators.anotations.impl;

import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.validators.anotations.TypeArtistsValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.function.Predicate;

/**
 * Implementación de la interfaz ConstraintValidator para validar listas de artistas.
 *
 * Esta clase verifica que los elementos de una lista de artistas cumplan con el tipo
 * especificado en la anotación {@link TypeArtistsValid}.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class TypeArtistsValidImpl implements ConstraintValidator<TypeArtistsValid, List<ArtistDto>> {
    private String entry;

    /**
     * Inicializa el validador con el tipo de artista objetivo definido en la anotación {@link TypeArtistsValid}.
     *
     * @param constraintAnnotation La anotación {@link TypeArtistsValid} que contiene la configuración.
     */
    @Override
    public void initialize(TypeArtistsValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        entry = constraintAnnotation.target().name();
    }

    /**
     * Valida una lista de artistas verificando que no existan elementos con un tipo diferente al especificado.
     *
     * @param artistListDto            La lista de artistas a validar.
     * @param constraintValidatorContext El contexto de validación.
     * @return true si la lista es válida, false en caso contrario.
     */
    @Override
    public boolean isValid(List<ArtistDto> artistListDto, ConstraintValidatorContext constraintValidatorContext) {
        // Predicado que verifica si el tipo de artista coincide con el tipo objetivo.
        final Predicate<String> isArtis = entry::equalsIgnoreCase;
        // Predicado que verifica si hay algún tipo de artista diferente al tipo objetivo.
        final Predicate<String> thereAreAnyManager = isArtis.negate();

        // Retorna true si la lista no es nula y no contiene elementos con un tipo diferente al objetivo.
        return artistListDto != null && !artistListDto.isEmpty() && artistListDto.stream().map(ArtistDto::getTypeArtist)
                .noneMatch(thereAreAnyManager);
    }
}