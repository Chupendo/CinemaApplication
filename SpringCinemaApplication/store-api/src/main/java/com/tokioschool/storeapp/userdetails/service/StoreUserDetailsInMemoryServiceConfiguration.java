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

/**
 * Configuración del servicio de detalles de usuario en memoria.
 *
 * Esta clase configura un servicio de detalles de usuario en memoria utilizando
 * los usuarios definidos en las propiedades de configuración de la aplicación.
 *
 * @version andres.rpenuela
 * @version 1.0
 */
@Configuration
@RequiredArgsConstructor
public class StoreUserDetailsInMemoryServiceConfiguration {

    private final StoreUserConfigurationProperty storeUserConfigurationProperty;

    /**
     * Define un bean para el servicio de detalles de usuario.
     *
     * Este metodo convierte los usuarios definidos en las propiedades de configuración
     * en objetos `UserDetails` de Spring Security y los gestiona en memoria.
     *
     * @return Una instancia de `InMemoryUserDetailsManager` que contiene los usuarios configurados.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // Convierte los usuarios definidos en memoria a objetos UserDetails de Spring Security
        List<UserDetails> users = storeUserConfigurationProperty.users().stream()
                .map(user ->
                        User.builder()
                                .username(user.username())
                                .password(user.password())
                                .authorities(getAuthoritiesUser(user))
                                .build()
                ).toList();

        return new InMemoryUserDetailsManager(users);
    }

    /**
     * Obtiene las autoridades y roles de un usuario.
     *
     * Este metodo combina las autoridades y roles de un usuario en un único arreglo de cadenas.
     * Los roles se prefijan con "ROLE_" según las convenciones de Spring Security.
     *
     * @param user El objeto `UserDto` que representa al usuario.
     * @return Un arreglo de cadenas que contiene las autoridades y roles del usuario.
     */
    private static String[] getAuthoritiesUser(UserDto user) {
        String[] privilegesArray = user.authorities().stream().map(StringUtils::upperCase).toArray(String[]::new);
        String[] rolesArray = user.roles().stream().map(StringUtils::upperCase).map("ROLE_"::concat).toArray(String[]::new);

        return ArrayUtils.addAll(privilegesArray, rolesArray);
    }
}