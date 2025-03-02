package com.tokioschool.ratingapp.configurations.convertes;

import com.tokioschool.ratingapp.domains.Role;
import com.tokioschool.ratingapp.dto.roles.RoleDto;
import com.tokioschool.ratingapp.helpers.BuildDtoHelper;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Converter class to convert a Set of Role objects to a List of RoleDto objects.
 */
public class SetRoleToListRoleDtoConverter implements Converter<Set<Role>, List<RoleDto>> {

    /**
     * Converts a Set of Role objects to a List of RoleDto objects.
     *
     * @param context the mapping context containing the source Set of Role objects
     * @return a List of RoleDto objects, or an empty list if the source set is null
     */
    @Override
    public List<RoleDto> convert(MappingContext<Set<Role>, List<RoleDto>> context) {
        return Optional.ofNullable(context)
                .map(MappingContext::getSource)
                .map(sourceSet -> sourceSet.stream()
                        .map(BuildDtoHelper::buildRoleDto)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}