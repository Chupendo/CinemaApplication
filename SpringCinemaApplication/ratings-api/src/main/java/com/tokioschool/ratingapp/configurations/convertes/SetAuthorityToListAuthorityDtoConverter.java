package com.tokioschool.ratingapp.configurations.convertes;

import com.tokioschool.ratingapp.domains.Authority;
import com.tokioschool.ratingapp.dto.authorities.AuthorityDto;
import com.tokioschool.ratingapp.helpers.BuildDtoHelper;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Converter class to convert a Set of Authority objects to a List of AuthorityDto objects.
 */
public class SetAuthorityToListAuthorityDtoConverter implements Converter<Set<Authority>, List<AuthorityDto>> {

    /**
     * Converts a Set of Authority objects to a List of AuthorityDto objects.
     *
     * @param context the mapping context containing the source Set of Authority objects
     * @return a List of AuthorityDto objects, or an empty list if the context or source set is null
     */
    @Override
    public List<AuthorityDto> convert(MappingContext<Set<Authority>, List<AuthorityDto>> context) {
        return Optional.ofNullable(context)
                .map(MappingContext::getSource)
                .map(sourceSet -> sourceSet.stream()
                        .map(BuildDtoHelper::buildAuthorityDto)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}
