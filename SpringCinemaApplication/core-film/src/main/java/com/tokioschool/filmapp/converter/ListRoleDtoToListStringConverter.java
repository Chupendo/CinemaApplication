package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.dto.user.RoleDto;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ListRoleDtoToListStringConverter implements Converter<List<RoleDto>, List<String>> {
    @Override
    public List<String> convert(MappingContext<List<RoleDto>, List<String>> context) {
        return Optional.ofNullable(context)
                .map(MappingContext::getSource)
                .stream()
                .flatMap(Collection::stream)
                .map(RoleDto::getName)
                .map(String::toUpperCase)
                .collect(Collectors.toList());
    }
}
