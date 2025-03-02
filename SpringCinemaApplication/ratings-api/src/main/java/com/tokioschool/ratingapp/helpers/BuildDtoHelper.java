package com.tokioschool.ratingapp.helpers;

import com.tokioschool.ratingapp.domains.Authority;
import com.tokioschool.ratingapp.domains.Role;
import com.tokioschool.ratingapp.dto.authorities.AuthorityDto;
import com.tokioschool.ratingapp.dto.roles.RoleDto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Helper class for building DTOs.
 */
public class BuildDtoHelper {

    /**
     * Private constructor to prevent instantiation.
     */
    private BuildDtoHelper() {
    }

    /**
     * Converts an Authority object to an AuthorityDto object.
     *
     * @param authority the Authority object to be converted
     * @return the converted AuthorityDto object, or null if the input is null
     */
    public static AuthorityDto buildAuthorityDto(Authority authority) {

        return Optional.ofNullable(authority)
                .map(source -> AuthorityDto.builder()
                        .id(source.getId())
                        .name(source.getName())
                        .build()
                ).orElseGet(() -> null);
    }

    /**
     * Converts a Role object to a RoleDto object.
     *
     * @param role the Role object to be converted
     * @return the converted RoleDto object, or null if the input is null
     */
    public static RoleDto buildRoleDto(Role role) {

        return Optional.ofNullable(role)
                .map(source -> RoleDto.builder()
                        .id(source.getId())
                        .name(source.getName())
                        .authorities(buildAuthorityDtoList(source.getAuthorities()))
                        .build()
                ).orElseGet(() -> null);
    }

    /**
     * Converts a Set of Authority objects to a List of AuthorityDto objects.
     *
     * @param authorities the Set of Authority objects to be converted
     * @return a List of AuthorityDto objects, or an empty list if the input set is null
     */
    public static List<AuthorityDto> buildAuthorityDtoList(Set<Authority> authorities) {
        return Optional.ofNullable(authorities)
                .map(sourceStream -> sourceStream
                        .stream()
                        .map(com.tokioschool.ratingapp.helpers.BuildDtoHelper::buildAuthorityDto)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}
