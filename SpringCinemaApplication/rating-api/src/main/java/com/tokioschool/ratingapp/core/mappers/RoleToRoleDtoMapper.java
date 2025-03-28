package com.tokioschool.ratingapp.core.mappers;


import com.tokioschool.ratingapp.core.converters.SetAuthorityToListAuthorityDtoConverter;
import com.tokioschool.ratingapp.core.converters.SetScopesToListScopeDtoConverter;
import com.tokioschool.ratingapp.domains.Role;
import com.tokioschool.ratingapp.dtos.RoleDto;
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
                        mapping.using(new SetAuthorityToListAuthorityDtoConverter()).map(Role::getAuthorities, RoleDto::setAuthorities))
                .addMappings(mapping ->
                        mapping.using(new SetScopesToListScopeDtoConverter()).map(Role::getScopes, RoleDto::setScopes));
    }
}