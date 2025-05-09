package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.domain.Role;
import com.tokioschool.filmapp.dto.user.RoleDto;
import com.tokioschool.helpers.BuildDtoHelper;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Clase convertidora que transforma un conjunto (\{@link Set\}) de objetos \{@link Role\}
 * en una lista (\{@link List\}) de objetos \{@link RoleDto\}.
 *
 * Esta clase implementa la interfaz \{@link Converter\} de ModelMapper para realizar la conversión.
 * Es útil para mapear entidades Role a sus representaciones DTO.
 *
 * @author andres
 * @version 1.0
 */
public class SetRoleToListRoleDtoConverter implements Converter<Set<Role>, List<RoleDto>> {

    /**
     * Realiza la conversión de un conjunto de \{@link Role\} a una lista de \{@link RoleDto\}.
     *
     * @param context El contexto de mapeo proporcionado por ModelMapper, que contiene
     *                la fuente (\{@link Set\} de \{@link Role\}) y el destino (\{@link List\} de \{@link RoleDto\}).
     * @return Una lista de \{@link RoleDto\}, o una lista vacía si el contexto o la fuente son nulos.
     */
    @Override
    public List<RoleDto> convert(MappingContext<Set<Role>, List<RoleDto>> context) {
        return Optional.ofNullable(context) // Verifica si el contexto no es nulo
                .map(MappingContext::getSource) // Obtiene la fuente del contexto
                .map(sourceSet -> sourceSet.stream() // Convierte el conjunto en un flujo
                        .map(BuildDtoHelper::buildRoleDto) // Mapea cada Role a su RoleDto
                        .collect(Collectors.toList())) // Recoge los resultados en una lista
                .orElse(Collections.emptyList()); // Devuelve una lista vacía si la fuente es nula
    }
}