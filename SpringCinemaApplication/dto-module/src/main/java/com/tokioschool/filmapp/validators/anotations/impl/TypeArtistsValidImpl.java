package com.tokioschool.filmapp.validators.anotations.impl;

import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.validators.anotations.TypeArtistsValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.function.Predicate;

public class TypeArtistsValidImpl implements ConstraintValidator<TypeArtistsValid, List<ArtistDto>> {
    private String entry;

    @Override
    public void initialize(TypeArtistsValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        entry = constraintAnnotation.target().name();
    }

    @Override
    public boolean isValid(List<ArtistDto> artistListDto, ConstraintValidatorContext constraintValidatorContext) {
        final Predicate<String> isArtis = entry::equalsIgnoreCase;
        final Predicate<String> thereAreAnyManager = isArtis.negate();

        return artistListDto != null && artistListDto.stream().map(ArtistDto::getTypeArtist)
                .noneMatch(thereAreAnyManager);
    }
}