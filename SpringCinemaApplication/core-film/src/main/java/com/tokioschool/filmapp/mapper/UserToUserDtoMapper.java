package com.tokioschool.filmapp.mapper;

import com.tokioschool.filmapp.converter.RolesSetToRolesDtoListConverter;
import com.tokioschool.filmapp.converter.UUIDToStringConverter;
import com.tokioschool.filmapp.domain.User;
import com.tokioschool.filmapp.dto.user.UserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for mapper the fields of User to UserDto
 *
 * @version 1.0
 * @author andres.rpenuela
 */
@Configuration
public class UserToUserDtoMapper {

    // override to bean of ModelMapper of ModelMapperConfiguration
    private final ModelMapper modelMapper;

    public UserToUserDtoMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
        configureUserDTOConverter();
    }

    private void configureUserDTOConverter(){
        modelMapper.typeMap(User.class, UserDTO.class)
                .addMappings(mapping ->
                        mapping.using(new RolesSetToRolesDtoListConverter() )
                                .map(User::getRoles,UserDTO::setRoles))
                .addMappings(mapping ->
                        mapping.using(new UUIDToStringConverter() )
                                .map(User::getImage,UserDTO::setResourceId));
    }
}
