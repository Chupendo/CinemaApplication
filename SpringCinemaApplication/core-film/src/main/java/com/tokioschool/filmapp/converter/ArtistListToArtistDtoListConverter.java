package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.domain.Artist;
import com.tokioschool.filmapp.dto.artist.ArtistDto;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Convertidor para transformar una lista de objetos {@link Artist} en una lista de objetos {@link ArtistDto}.
 * Implementa la interfaz {@link Converter} de ModelMapper.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class ArtistListToArtistDtoListConverter implements Converter<List<Artist>, List<ArtistDto>> {

    /**
     * Convierte una lista de entidades {@link Artist} en una lista de DTOs {@link ArtistDto}.
     *
     * @param context El contexto de mapeo que contiene la lista de origen ({@link List<Artist>}).
     * @return Una lista de objetos {@link ArtistDto} mapeados desde la lista de origen.
     */
    @Override
    public List<ArtistDto> convert(MappingContext<List<Artist>, List<ArtistDto>> context) {
        // Instancia de ModelMapper para realizar el mapeo entre entidades y DTOs
        final ModelMapper modelMapper = new ModelMapper();

        // Convierte la lista de artistas en una lista de ArtistDto
        return Optional.ofNullable(context) // Verifica si el contexto no es nulo
                .map(MappingContext::getSource) // Obtiene la lista de origen del contexto
                .stream() // Convierte el Optional en un Stream
                .flatMap(Collection::stream) // Descompone la colección en elementos individuales
                .map(artist -> modelMapper.map(artist, ArtistDto.class)) // Mapea cada artista a un ArtistDto
                .toList(); // Recoge los resultados en una lista
    }
}