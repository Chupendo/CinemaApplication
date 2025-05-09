package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.enums.TYPE_ARTIST;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.Optional;

/**
 * Clase convertidora que transforma un objeto de tipo \{@link TYPE_ARTIST\}
 * en su representación en cadena (\{@link String\}).
 *
 * Esta clase implementa la interfaz \{@link Converter\} de ModelMapper para realizar la conversión.
 * Es útil para mapear enumeraciones TYPE_ARTIST a cadenas en mayúsculas.
 *
 * @author andres
 * @version 1.0
 */
public class TypeArtistToStringConverter implements Converter<TYPE_ARTIST, String> {

    /**
     * Realiza la conversión de un objeto \{@link TYPE_ARTIST\} a una cadena en mayúsculas.
     *
     * @param context El contexto de mapeo proporcionado por ModelMapper, que contiene
     *                la fuente (\{@link TYPE_ARTIST\}) y el destino (\{@link String\}).
     * @return Una cadena en mayúsculas que representa el nombre del tipo de artista,
     *         o null si el contexto o la fuente son nulos.
     */
    @Override
    public String convert(MappingContext<TYPE_ARTIST, String> context) {
        return Optional.ofNullable(context) // Verifica si el contexto no es nulo
                .map(MappingContext::getSource) // Obtiene la fuente del contexto
                .map(TYPE_ARTIST::name) // Obtiene el nombre del tipo de artista
                .map(String::toUpperCase) // Convierte el nombre a mayúsculas
                .orElseGet(() -> null); // Devuelve null si la fuente es nula
    }
}