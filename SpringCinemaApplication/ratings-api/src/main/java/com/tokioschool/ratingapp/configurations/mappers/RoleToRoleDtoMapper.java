package com.tokioschool.ratingapp.configurations.mappers;

import com.tokioschool.ratingapp.configurations.convertes.SetAuthorityToListAuthorityDtoConverter;
import com.tokioschool.ratingapp.domains.Role;
import com.tokioschool.ratingapp.dto.roles.RoleDto;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoleToRoleDtoMapper {

    private final ModelMapper modelMapper;

    /**
     * Constructor to initialize the ModelMapper and configure the RoleDto converter.
     *
     * @param modelMapper the ModelMapper instance to be used for mapping
     */
    public RoleToRoleDtoMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
        configureRoleDtoConverter();
    }

    /**
     * Configures the ModelMapper to convert Role to RoleDto.
     */
    private void configureRoleDtoConverter() {
        modelMapper.typeMap(Role.class, RoleDto.class)
                .addMappings(mapping ->
                        mapping.using(new SetAuthorityToListAuthorityDtoConverter()).map(Role::getAuthorities, RoleDto::setAuthorities));
    }
}