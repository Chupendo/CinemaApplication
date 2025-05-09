package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.domain.Authority;
import com.tokioschool.helpers.BuildDtoHelper;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Clase convertidora que transforma un conjunto (\{@link Set\}) de objetos \{@link Authority\}
 * en una lista (\{@link List\}) de objetos AuthorityDto.
 *
 * Esta clase implementa la interfaz \{@link Converter\} de ModelMapper para realizar la conversión.
 * Es útil para mapear entidades Authority a sus representaciones DTO.
 *
 * @author andres
 * @version 1.0
 */
public class SetAuthorityToListAuthorityDtoConverter implements Converter<Set<Authority>, List<String>> {

    /**
     * Realiza la conversión de un conjunto de \{@link Authority\} a una lista de AuthorityDto.
     *
     * @param context El contexto de mapeo proporcionado por ModelMapper, que contiene
     *                la fuente (\{@link Set\} de \{@link Authority\}) y el destino (\{@link List\} de AuthorityDto).
     * @return Una lista de AuthorityDto, o una lista vacía si el contexto o la fuente son nulos.
     */
    @Override
    public List<String> convert(MappingContext<Set<Authority>, List<String>> context) {
        return Optional.ofNullable(context) // Verifica si el contexto no es nulo
                .map(MappingContext::getSource) // Obtiene la fuente del contexto
                .map(sourceSet -> sourceSet.stream() // Convierte el conjunto en un flujo
                        .map(BuildDtoHelper::buildAuthorityDto) // Mapea cada Authority a su AuthorityDto
                        .collect(Collectors.toList())) // Recoge los resultados en una lista
                .orElse(Collections.emptyList()); // Devuelve una lista vacía si la fuente es nula
    }
}