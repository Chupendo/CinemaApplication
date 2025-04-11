package com.tokioschool.filmapp.converter;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.Optional;
import java.util.UUID;

/**
 * Clase convertidora que transforma un objeto de tipo \{@link UUID\}
 * en su representación en cadena (\{@link String\}).
 *
 * Esta clase implementa la interfaz \{@link Converter\} de ModelMapper para realizar la conversión.
 * Es útil para mapear identificadores únicos universales (UUID) a cadenas,
 * que se utilizan como identificadores de recursos.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class UUIDToStringConverter implements Converter<UUID, String> {

    /**
     * Realiza la conversión de un objeto \{@link UUID\} a una cadena.
     *
     * @param context El contexto de mapeo proporcionado por ModelMapper, que contiene
     *                la fuente (\{@link UUID\}) y el destino (\{@link String\}).
     * @return Una cadena que representa el UUID, o null si el contexto o la fuente son nulos.
     */
    @Override
    public String convert(MappingContext<UUID, String> context) {
        return Optional.ofNullable(context) // Verifica si el contexto no es nulo
                .map(MappingContext::getSource) // Obtiene la fuente del contexto
                .map(UUID::toString) // Convierte el UUID a su representación en cadena
                .orElseGet(() -> null); // Devuelve null si la fuente es nula
    }
}