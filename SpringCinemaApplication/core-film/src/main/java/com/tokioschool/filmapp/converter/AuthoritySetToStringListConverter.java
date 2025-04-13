package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.domain.Authority;
import com.tokioschool.filmapp.domain.Role;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Convertidor que transforma un conjunto (\{@link Set\}) de objetos \{@link Authority\}
 * en una lista (\{@link List\}) de cadenas que representan los nombres de las autoridades.
 *
 * Esta clase implementa la interfaz \{@link Converter\} de ModelMapper para realizar la conversión.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class AuthoritySetToStringListConverter implements Converter<Set<Authority>, List<String>> {

    /**
     * Realiza la conversión de un conjunto de \{@link Authority\} a una lista de cadenas.
     *
     * @param context El contexto de mapeo proporcionado por ModelMapper, que contiene
     *                la fuente (\{@link Set\} de \{@link Authority\}) y el destino (\{@link List\} de cadenas).
     * @return Una lista de cadenas que representan los nombres de las autoridades, o una lista vacía
     *         si el contexto o la fuente son nulos.
     */
    @Override
    public List<String> convert(MappingContext<Set<Authority>, List<String>> context) {
        return Optional.ofNullable(context) // Verifica si el contexto no es nulo
                .map(MappingContext::getSource) // Obtiene la fuente del contexto
                .stream() // Convierte el Optional en un Stream
                .flatMap(Collection::stream) // Descompone el conjunto en un flujo de elementos
                .map(Authority::getName) // Mapea cada autoridad a su nombre
                .toList(); // Recoge los resultados en una lista
    }
}