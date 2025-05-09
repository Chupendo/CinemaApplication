package com.tokioschool.filmapp.mapper;

import com.tokioschool.filmapp.converter.SetRoleToListRoleDtoConverter;
import com.tokioschool.filmapp.converter.UUIDToStringConverter;
import com.tokioschool.filmapp.domain.User;
import com.tokioschool.filmapp.dto.user.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para mapear los campos de la entidad \{@link User\} a su DTO \{@link UserDto\}.
 *
 * Esta clase utiliza \{@link ModelMapper\} para definir las reglas de conversión entre
 * las propiedades de la entidad User y su representación en UserDto.
 * También utiliza convertidores personalizados como:
 * - \{@link SetRoleToListRoleDtoConverter\} para mapear un conjunto de roles a una lista de DTOs de roles.
 * - \{@link UUIDToStringConverter\} para mapear un UUID a su representación en cadena.
 *
 * @version 1.0
 * @author
 */
@Configuration
public class UserToUserDtoMapper {

    private final ModelMapper modelMapper;

    /**
     * Constructor que inicializa el mapper con el bean de \{@link ModelMapper\}.
     *
     * @param modelMapper El bean de ModelMapper utilizado para configurar las reglas de mapeo.
     */
    public UserToUserDtoMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
        configureUserDtoConverter();
    }

    /**
     * Configura el \{@link ModelMapper\} para convertir un objeto de tipo \{@link User\} a \{@link UserDto\}.
     *
     * Este método define las siguientes reglas de mapeo personalizadas:
     * - Mapea el campo roles de User al campo roles de UserDto utilizando \{@link SetRoleToListRoleDtoConverter\}.
     * - Mapea el campo image de User al campo resourceId de UserDto utilizando \{@link UUIDToStringConverter\}.
     */
    private void configureUserDtoConverter() {
        modelMapper.typeMap(User.class, UserDto.class)
                .addMappings(mapping ->
                        mapping.using(new SetRoleToListRoleDtoConverter()).map(User::getRoles, UserDto::setRoles))
                .addMappings(mapping ->
                        mapping.using(new UUIDToStringConverter()).map(User::getImage, UserDto::setResourceId));
    }
}