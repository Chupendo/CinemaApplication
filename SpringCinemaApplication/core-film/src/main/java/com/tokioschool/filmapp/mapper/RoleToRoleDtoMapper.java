package com.tokioschool.filmapp.mapper;

import com.tokioschool.filmapp.converter.SetAuthorityToListAuthorityDtoConverter;
import com.tokioschool.filmapp.converter.SetScopesToListScopeDtoConverter;
import com.tokioschool.filmapp.domain.Role;
import com.tokioschool.filmapp.dto.user.RoleDto;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para mapear los campos de la entidad \{@link Role\} a su DTO \{@link RoleDto\}.
 *
 * Esta clase utiliza \{@link ModelMapper\} para definir las reglas de conversión entre
 * las propiedades de la entidad Role y su representación en RoleDto.
 * También utiliza convertidores personalizados como:
 * - \{@link SetAuthorityToListAuthorityDtoConverter\} para mapear un conjunto de autoridades a una lista de DTOs de autoridades.
 * - \{@link SetScopesToListScopeDtoConverter\} para mapear un conjunto de alcances a una lista de DTOs de alcances.
 *
 * @version 1.0
 * @author andres
 */
@Configuration
public class RoleToRoleDtoMapper {

    private final ModelMapper modelMapper;

    /**
     * Constructor que inicializa el mapper con el bean de \{@link ModelMapper\}.
     *
     * @param modelMapper El bean de ModelMapper utilizado para configurar las reglas de mapeo.
     */
    public RoleToRoleDtoMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
        configureRoleDtoConverter();
    }

    /**
     * Configura el \{@link ModelMapper\} para convertir un objeto de tipo \{@link Role\} a \{@link RoleDto\}.
     *
     * Este método define las siguientes reglas de mapeo personalizadas:
     * - Mapea el campo authorities de Role al campo authorities de RoleDto utilizando \{@link SetAuthorityToListAuthorityDtoConverter\}.
     * - Mapea el campo scopes de Role al campo scopes de RoleDto utilizando \{@link SetScopesToListScopeDtoConverter\}.
     */
    private void configureRoleDtoConverter() {
        modelMapper.typeMap(Role.class, RoleDto.class)
                .addMappings(mapping ->
                        mapping.using(new SetAuthorityToListAuthorityDtoConverter()).map(Role::getAuthorities, RoleDto::setAuthorities))
                .addMappings(mapping ->
                        mapping.using(new SetScopesToListScopeDtoConverter()).map(Role::getScopes, RoleDto::setScopes));
    }
}