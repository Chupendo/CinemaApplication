package com.tokioschool.helpers;

import com.tokioschool.filmapp.domain.Authority;
import com.tokioschool.filmapp.domain.Role;
import com.tokioschool.filmapp.domain.Scope;
import com.tokioschool.filmapp.dto.user.RoleDto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Clase auxiliar para construir objetos DTO (Data Transfer Object).
 *
 * Proporciona métodos estáticos para convertir entidades del dominio
 * en sus correspondientes representaciones DTO.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class BuildDtoHelper {

    /**
     * Constructor privado para evitar la instanciación de la clase.
     */
    private BuildDtoHelper() {
    }

    /**
     * Convierte un objeto de tipo \{@link Authority\} en su representación como cadena.
     *
     * @param authority el objeto \{@link Authority\} a convertir
     * @return el nombre de la autoridad como cadena, o null si la entrada es null
     */
    public static String buildAuthorityDto(Authority authority) {
        return Optional.ofNullable(authority)
                .map(Authority::getName)
                .orElseGet(() -> null);
    }

    /**
     * Convierte un objeto de tipo \{@link Scope\} en su representación como cadena.
     *
     * @param scope el objeto \{@link Scope\} a convertir
     * @return el nombre del alcance como cadena, o null si la entrada es null
     */
    public static String buildScopeDto(Scope scope) {
        return Optional.ofNullable(scope)
                .map(Scope::getName)
                .orElseGet(() -> null);
    }

    /**
     * Convierte un objeto de tipo \{@link Role\} en un objeto \{@link RoleDto\}.
     *
     * @param role el objeto \{@link Role\} a convertir
     * @return el objeto \{@link RoleDto\} convertido, o null si la entrada es null
     */
    public static RoleDto buildRoleDto(Role role) {
        return Optional.ofNullable(role)
                .map(source -> RoleDto.builder()
                        .name(source.getName())
                        .authorities(buildAuthorityDtoList(source.getAuthorities()))
                        .scopes(buildScopeDtoList(source.getScopes()))
                        .build()
                ).orElseGet(() -> null);
    }

    /**
     * Convierte un conjunto de objetos \{@link Scope\} en una lista de cadenas.
     *
     * @param scopes el conjunto de objetos \{@link Scope\} a convertir
     * @return una lista de nombres de alcance como cadenas, o una lista vacía si la entrada es null
     */
    private static List<String> buildScopeDtoList(Set<Scope> scopes) {
        return Optional.ofNullable(scopes)
                .map(sourceStream -> sourceStream
                        .stream()
                        .map(BuildDtoHelper::buildScopeDto)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    /**
     * Convierte un conjunto de objetos \{@link Authority\} en una lista de cadenas.
     *
     * @param authorities el conjunto de objetos \{@link Authority\} a convertir
     * @return una lista de nombres de autoridad como cadenas, o una lista vacía si la entrada es null
     */
    public static List<String> buildAuthorityDtoList(Set<Authority> authorities) {
        return Optional.ofNullable(authorities)
                .map(sourceStream -> sourceStream
                        .stream()
                        .map(BuildDtoHelper::buildAuthorityDto)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}