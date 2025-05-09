package com.tokioschool.filmapp.services.securities;

import com.tokioschool.filmapp.dto.user.RoleDto;
import com.tokioschool.filmapp.dto.user.UserDto;
import com.tokioschool.filmapp.services.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Servicio para la autenticación de usuarios en la aplicación de películas.
 *
 * Esta clase implementa la interfaz {@link UserDetailsService} de Spring Security
 * para cargar los detalles de un usuario a partir de su nombre de usuario o correo electrónico.
 *
 * Anotaciones:
 * - {@link Service}: Marca esta clase como un componente de servicio de Spring.
 * - {@link RequiredArgsConstructor}: Genera un constructor con los campos finales requeridos.
 * - {@link Slf4j}: Proporciona un logger para la clase.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Service("filmUserDetails")
@RequiredArgsConstructor
@Slf4j
public class FilmUserDetailsService implements UserDetailsService {

    private final UserService userService;

    /**
     * Carga los detalles de un usuario a partir de su nombre de usuario o correo electrónico.
     *
     * @param username Nombre de usuario o correo electrónico del usuario a autenticar.
     * @return Una instancia de {@link UserDetails} que contiene los detalles del usuario.
     * @throws UsernameNotFoundException Si el usuario no existe en el sistema.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Pair<UserDto, String> tupleUserDto = userService.findUserAndPasswordByEmail(username)
                    .orElseThrow(() -> new IllegalArgumentException("User with surname: %s not found".formatted(username)));

            // Se adapta a UserDetails de Spring Security
            return toUserDetails(tupleUserDto.getLeft(), tupleUserDto.getRight());

        } catch (IllegalArgumentException e) {
            log.error("user {} not authorized, because: {} ", username, e.getMessage(), e);
            throw new UsernameNotFoundException("Credenciales erroneas: %s, because: {}".formatted(username), e);
        }
    }

    /**
     * Convierte un objeto {@link UserDto} y su contraseña en una instancia de {@link UserDetails}.
     *
     * @param userDto Objeto que representa los datos del usuario.
     * @param password Contraseña del usuario (encriptada).
     * @return Una instancia de {@link UserDetails} adaptada a Spring Security.
     */
    private UserDetails toUserDetails(UserDto userDto, String password) {
        return new org.springframework.security.core.userdetails.User(
                userDto.getEmail(), // Identidad
                password, // Credenciales (encriptadas)
                getAuthoritiesUser(userDto) // Autoridades
        );
    }

    /**
     * Obtiene las autoridades del usuario a partir de sus roles, privilegios y alcances.
     *
     * @param userDto Objeto que representa los datos del usuario.
     * @return Una lista de objetos {@link SimpleGrantedAuthority} que representan las autoridades del usuario.
     */
    private static List<SimpleGrantedAuthority> getAuthoritiesUser(UserDto userDto) {

        // Roles
        List<SimpleGrantedAuthority> roles = Optional.ofNullable(userDto)
                .map(UserDto::getRoles)
                .stream()
                .flatMap(Collection::stream)
                .filter(Objects::nonNull) // Previene NPE si un role es null
                .map(RoleDto::getName)
                .filter(Objects::nonNull) // Por si hay names null
                .map(StringUtils::upperCase).map("ROLE_"::concat)
                .map(SimpleGrantedAuthority::new) // Mapea cada String a SimpleGrantedAuthority
                .toList();

        // Privilegios
        List<SimpleGrantedAuthority> privileges = Optional.ofNullable(userDto)
                .map(UserDto::getRoles)
                .stream()
                .flatMap(Collection::stream)
                .filter(Objects::nonNull) // Previene NPE si un role es null
                .map(RoleDto::getAuthorities)
                .filter(Objects::nonNull) // Previene NPE si getScopes() devuelve null
                .flatMap(Collection::stream)
                .filter(Objects::nonNull) // Por si hay authorities null
                .map(StringUtils::upperCase)
                .map(SimpleGrantedAuthority::new)
                .toList();

        // Alcances
        List<SimpleGrantedAuthority> scopes = Optional.ofNullable(userDto)
                .map(UserDto::getRoles)
                .stream()
                .flatMap(Collection::stream)
                .filter(Objects::nonNull) // Previene NPE si un role es null
                .map(RoleDto::getScopes)
                .filter(Objects::nonNull) // Previene NPE si getScopes() devuelve null
                .flatMap(Collection::stream)
                .filter(Objects::nonNull) // Por si hay scopes null
                .map(StringUtils::upperCase)
                .map("SCOPE_"::concat)
                .map(SimpleGrantedAuthority::new)
                .toList();

        // Combina roles, privilegios y alcances en una sola lista
        return Stream.of(roles, privileges, scopes)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}