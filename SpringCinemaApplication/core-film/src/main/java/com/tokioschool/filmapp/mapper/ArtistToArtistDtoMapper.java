package com.tokioschool.filmapp.mapper;

import com.tokioschool.filmapp.converter.TypeArtistToStringConverter;
import com.tokioschool.filmapp.domain.Artist;
import com.tokioschool.filmapp.dto.artist.ArtistDto;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para mapear los campos de la entidad \{@link Artist\} a su DTO \{@link ArtistDto\}.
 *
 * Esta clase utiliza \{@link ModelMapper\} para definir las reglas de conversión entre
 * las propiedades de la entidad Artist y su representación en ArtistDto.
 * También utiliza un convertidor personalizado \{@link TypeArtistToStringConverter\}
 * para transformar el tipo de artista en una cadena.
 *
 * @version 1.0
 * @author andres
 */
@Configuration
public class ArtistToArtistDtoMapper {

    // Sobrescribe el bean de ModelMapper de ModelMapperConfiguration
    private final ModelMapper modelMapper;

    /**
     * Constructor que inicializa el mapper con el bean de \{@link ModelMapper\}.
     *
     * @param modelMapper El bean de ModelMapper utilizado para configurar las reglas de mapeo.
     */
    public ArtistToArtistDtoMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
        configureUserDTOConverter();
    }

    /**
     * Configura el convertidor personalizado para mapear el campo typeArtist
     * de la entidad \{@link Artist\} al campo correspondiente en \{@link ArtistDto\}.
     *
     * Este metodo utiliza \{@link TypeArtistToStringConverter\} para realizar la conversión
     * de TYPE_ARTIST a una cadena.
     */
    private void configureUserDTOConverter(){
        modelMapper.typeMap(Artist.class, ArtistDto.class)
                .addMappings(mapping ->
                        mapping.using(new TypeArtistToStringConverter() )
                                .map(Artist::getTypeArtist, ArtistDto::setTypeArtist));
    }
}