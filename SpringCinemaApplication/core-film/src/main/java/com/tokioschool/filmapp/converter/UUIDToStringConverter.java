package com.tokioschool.filmapp.converter;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.Optional;
import java.util.UUID;

/**
 * Convert UUID to String, this used as id of resources
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class UUIDToStringConverter implements Converter<UUID, String> {
    @Override
    public String convert(MappingContext<UUID, String> context) {
        return Optional.ofNullable(context.getSource())
                .map(UUID::toString)
                .orElseGet(()->null);
    }
}
