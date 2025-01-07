package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.enums.TYPE_ARTIST;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class TypeArtistToStringConverter implements Converter<TYPE_ARTIST,String> {
    @Override
    public String convert(MappingContext<TYPE_ARTIST, String> context) {
        return context.getSource().name().toUpperCase();
    }
}
