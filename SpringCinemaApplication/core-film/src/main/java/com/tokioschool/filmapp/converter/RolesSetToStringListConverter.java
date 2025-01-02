package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.domain.Role;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.List;
import java.util.Set;

/**
 * Convert a collection Set of Roles to collection List of String with name of Roles
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class RolesSetToStringListConverter implements Converter<Set<Role>, List<String>> {

    @Override
    public List<String> convert(MappingContext<Set<Role>, List<String>> context) {
        return context.getSource().stream().map(Role::getName).toList();
    }
}
