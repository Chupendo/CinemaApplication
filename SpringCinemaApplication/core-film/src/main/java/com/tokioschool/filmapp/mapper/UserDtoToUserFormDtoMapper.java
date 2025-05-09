package com.tokioschool.filmapp.mapper;

import com.tokioschool.filmapp.converter.ListRoleDtoToListStringConverter;
import com.tokioschool.filmapp.dto.user.UserDto;
import com.tokioschool.filmapp.dto.user.UserFormDto;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserDtoToUserFormDtoMapper {

    private final ModelMapper modelMapper;

    public UserDtoToUserFormDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        init();
    }

    private void init(){
        this.modelMapper.typeMap(UserDto.class, UserFormDto.class)
                .addMappings(mapping -> mapping.map(UserDto::getResourceId, UserFormDto::setImage))
                .addMappings(mapping -> mapping
                        .using( new ListRoleDtoToListStringConverter() ).map(UserDto::getRoles, UserFormDto::setRoles)
                );
    }
}
