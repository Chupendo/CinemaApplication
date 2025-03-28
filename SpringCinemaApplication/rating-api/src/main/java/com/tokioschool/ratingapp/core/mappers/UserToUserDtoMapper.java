package com.tokioschool.ratingapp.core.mappers;


import com.tokioschool.ratingapp.core.converters.SetRoleToListRoleDtoConverter;
import com.tokioschool.ratingapp.domains.User;
import com.tokioschool.ratingapp.dtos.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for mapping User to UserDto.
 */
@Configuration
public class UserToUserDtoMapper {

    private final ModelMapper modelMapper;

    /**
     * Constructor to initialize the ModelMapper and configure the UserDto converter.
     *
     * @param modelMapper the ModelMapper instance to be used for mapping
     */
    public UserToUserDtoMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
        configureUserDtoConverter();
    }

    /**
     * Configures the ModelMapper to convert User to UserDto.
     */
    private void configureUserDtoConverter() {
        modelMapper.typeMap(User.class, UserDto.class)
                .addMappings(mapping ->
                        mapping.using(new SetRoleToListRoleDtoConverter()).map(User::getRoles, UserDto::setRoles));
    }
}
