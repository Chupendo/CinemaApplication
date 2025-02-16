package com.tokioschool.filmapp.validators.anotations.impl;

import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.validators.anotations.TypeArtistValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TypeArtistValidImpl implements ConstraintValidator<TypeArtistValid, ArtistDto> {
    private String entry;

    @Override
    public void initialize(TypeArtistValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        entry = constraintAnnotation.target().name();
    }

    @Override
    public boolean isValid(ArtistDto artistDto, ConstraintValidatorContext constraintValidatorContext) {
        return artistDto.getTypeArtist()!=null && artistDto.getTypeArtist().equalsIgnoreCase(entry);
    }
}
