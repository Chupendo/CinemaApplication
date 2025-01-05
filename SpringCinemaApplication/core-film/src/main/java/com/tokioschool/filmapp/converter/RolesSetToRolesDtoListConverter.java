package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.domain.Role;
import com.tokioschool.filmapp.dto.user.RoleDTO;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;

import java.util.List;
import java.util.Set;

/**
 * Convert a collection Set of Roles to collection List of String with name of Roles
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class RolesSetToRolesDtoListConverter implements Converter<Set<Role>, List<RoleDTO>> {


    @Override
    public List<RoleDTO> convert(MappingContext<Set<Role>, List<RoleDTO>> context) {
        final Set<Role> source = context.getSource();
        if (source == null) {
            return null;
        }

        final ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(Role.class, RoleDTO.class)
                .addMappings(mapping ->
                        mapping.using(new AuthoritySetToStringListConverter() )
                                .map(Role::getAuthorities,RoleDTO::setAuthorities));

        return source.stream()
                .map(role -> modelMapper.map(role, RoleDTO.class))
                .toList();
    }
}
