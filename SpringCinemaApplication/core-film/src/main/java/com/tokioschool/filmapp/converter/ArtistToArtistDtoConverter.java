package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.domain.Artist;
import com.tokioschool.filmapp.dto.artist.ArtistDto;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;

import java.util.Optional;

/**
 * Convertidor para transformar un objeto {@link Artist} en un objeto {@link ArtistDto}.
 * Implementa la interfaz {@link Converter} de ModelMapper.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class ArtistToArtistDtoConverter implements Converter<Artist, ArtistDto> {

    /**
     * Convierte una entidad {@link Artist} en un DTO {@link ArtistDto}.
     *
     * @param context El contexto de mapeo que contiene la entidad de origen ({@link Artist}).
     * @return Un objeto {@link ArtistDto} mapeado desde la entidad de origen, o {@code null} si el contexto es nulo.
     */
    @Override
    public ArtistDto convert(MappingContext<Artist, ArtistDto> context) {
        // Instancia de ModelMapper para realizar el mapeo entre la entidad y el DTO
        final ModelMapper modelMapper = new ModelMapper();

        // Convierte el objeto Artist en un ArtistDto, devolviendo null si el contexto es nulo
        return Optional.ofNullable(context)
                .map(MappingContext::getSource) // Obtiene la entidad de origen del contexto
                .stream() // Convierte el Optional en un Stream
                .map(artist -> modelMapper.map(artist, ArtistDto.class)) // Mapea el objeto Artist a ArtistDto
                .findAny() // Obtiene el primer elemento del Stream
                .orElseGet(() -> null); // Devuelve null si no hay elementos
    }
}