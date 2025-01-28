package com.tokioschool.storeapp.userdetails.service;

import com.tokioschool.storeapp.userdetails.dto.UserDto;
import com.tokioschool.storeapp.userdetails.properties.StoreUserConfigurationProperty;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class StoreUserDetailsInMemoryServiceConfiguration {

    private final StoreUserConfigurationProperty storeUserConfigurationProperty;

    @Bean
    public UserDetailsService  userDetailsService() {
        // convierte el usuario que hay en memoria a un user detials de spring
        List<UserDetails> users = storeUserConfigurationProperty.users().stream()
                .map(user ->
                        User.builder()
                                .username( user.username() )
                                .password( user.password() )
                                .authorities( getAuthoritiesUser(user) )
                                .build()
                ).toList();

        return new InMemoryUserDetailsManager(users);
    }

    private static String[] getAuthoritiesUser(UserDto user ) {
        String[] privilegesArray = user.authorities().stream().map(StringUtils::upperCase).toArray(String[]::new);
        String[] rolesArray = user.roles().stream().map(StringUtils::upperCase).map("ROLE_"::concat).toArray(String[]::new);

        return ArrayUtils.addAll(privilegesArray, rolesArray);

    }
}
