package com.tokioschool.filmapp.mapper;

import com.tokioschool.filmapp.converter.ArtistListToArtistDtoListConverter;
import com.tokioschool.filmapp.converter.ArtistToArtistDtoConverter;
import com.tokioschool.filmapp.converter.UUIDToStringConverter;
import com.tokioschool.filmapp.domain.Movie;
import com.tokioschool.filmapp.dto.movie.MovieDto;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para mapear los campos de la entidad \{@link Movie\} a su DTO \{@link MovieDto\}.
 *
 * Esta clase utiliza \{@link ModelMapper\} para definir las reglas de conversión entre
 * las propiedades de la entidad Movie y su representación en MovieDto.
 * También utiliza convertidores personalizados como:
 * - \{@link ArtistToArtistDtoConverter\} para mapear un artista a su DTO.
 * - \{@link ArtistListToArtistDtoListConverter\} para mapear una lista de artistas a una lista de DTOs.
 * - \{@link UUIDToStringConverter\} para mapear un UUID a su representación en cadena.
 *
 * @version 1.0
 * @author andres
 */
@Configuration
public class MovieToMovieDtoMapper {

    // Sobrescribe el bean de ModelMapper de ModelMapperConfiguration
    private final ModelMapper modelMapper;

    /**
     * Constructor que inicializa el mapper con el bean de \{@link ModelMapper\}.
     *
     * @param modelMapper El bean de ModelMapper utilizado para configurar las reglas de mapeo.
     */
    MovieToMovieDtoMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
        init();
    }

    /**
     * Inicializa las reglas de mapeo entre la entidad \{@link Movie\} y su DTO \{@link MovieDto\}.
     *
     * Este método configura los siguientes mapeos personalizados:
     * - Mapea el campo manager de Movie a managerDto de MovieDto utilizando \{@link ArtistToArtistDtoConverter\}.
     * - Mapea el campo artists de Movie a artistDtos de MovieDto utilizando \{@link ArtistListToArtistDtoListConverter\}.
     * - Mapea el campo image de Movie a resourceId de MovieDto utilizando \{@link UUIDToStringConverter\}.
     */
    private void init(){
        this.modelMapper.typeMap(Movie.class, MovieDto.class)
                .addMappings(mapping -> mapping.using(new ArtistToArtistDtoConverter())
                        .map(Movie::getManager,MovieDto::setManagerDto))
                .addMappings(mapping -> mapping.using(new ArtistListToArtistDtoListConverter())
                        .map(Movie::getArtists,MovieDto::setArtistDtos))
                .addMappings(mapping -> mapping.using(new UUIDToStringConverter())
                        .map(Movie::getImage,MovieDto::setResourceId));
    }

}