package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.domain.Authority;
import com.tokioschool.filmapp.domain.Role;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.List;
import java.util.Set;

/**
 * Convert a collection Set of Roles to collection List of String with name of Authorities
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class AuthoritySetToStringListConverter implements Converter<Set<Authority>, List<String>> {

    @Override
    public List<String> convert(MappingContext<Set<Authority>, List<String>> context) {
        return context.getSource().stream().map(Authority::getName).toList();
    }
}
