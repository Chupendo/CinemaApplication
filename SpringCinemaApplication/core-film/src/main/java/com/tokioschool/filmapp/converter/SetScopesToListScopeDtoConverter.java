package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.domain.Scope;
import com.tokioschool.helpers.BuildDtoHelper;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Clase convertidora que transforma un conjunto (\{@link Set\}) de objetos \{@link Scope\}
 * en una lista (\{@link List\}) de objetos ScopeDto.
 *
 * Esta clase implementa la interfaz \{@link Converter\} de ModelMapper para realizar la conversión.
 * Es útil para mapear entidades Scope a sus representaciones DTO.
 *
 * @author andres
 * @version 1.0
 */
public class SetScopesToListScopeDtoConverter implements Converter<Set<Scope>, List<String>> {

    /**
     * Realiza la conversión de un conjunto de \{@link Scope\} a una lista de ScopeDto.
     *
     * @param context El contexto de mapeo proporcionado por ModelMapper, que contiene
     *                la fuente (\{@link Set\} de \{@link Scope\}) y el destino (\{@link List\} de ScopeDto).
     * @return Una lista de ScopeDto, o una lista vacía si el contexto o la fuente son nulos.
     */
    @Override
    public List<String> convert(MappingContext<Set<Scope>, List<String>> context) {
        return Optional.ofNullable(context) // Verifica si el contexto no es nulo
                .map(MappingContext::getSource) // Obtiene la fuente del contexto
                .map(sourceSet -> sourceSet.stream() // Convierte el conjunto en un flujo
                        .map(BuildDtoHelper::buildScopeDto) // Mapea cada Scope a su ScopeDto
                        .collect(Collectors.toList())) // Recoge los resultados en una lista
                .orElse(Collections.emptyList()); // Devuelve una lista vacía si la fuente es nula
    }
}