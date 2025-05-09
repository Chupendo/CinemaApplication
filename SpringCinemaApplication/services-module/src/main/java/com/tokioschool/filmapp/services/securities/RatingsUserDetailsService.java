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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la autenticación de usuarios en el sistema de calificaciones.
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
@Service("ratingUserDetails")
@RequiredArgsConstructor
@Slf4j
public class RatingsUserDetailsService implements UserDetailsService {

    private final UserService userService;

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        // Aquí recuperas el usuario de la base de datos, por ejemplo, usando un repositorio JPA
//        if ("user".equals(username)) {
//            return User.builder()
//                    .username("user")
//                    .password("$2y$10$5cTVuveCa8qhlZPApRF6kO5OwzhgF9nTDx57WuGKrb/E/E93Gw8Zq")//""{bcrypt}$2a$10$LDe7.j92o47ur5obHpGzIuUqVY41wVeTe5ZyF7/Tw9fdtJwbG1Dsu") // Contraseña encriptada
//                    .roles("USER")
//                    .build();
//        }
//        throw new UsernameNotFoundException("User not found");
//    }

    /**
     * Carga los detalles de un usuario a partir de su nombre de usuario o correo electrónico.
     *
     * @param usernameOrEmail Nombre de usuario o correo electrónico del usuario a autenticar.
     * @return Una instancia de {@link UserDetails} que contiene los detalles del usuario.
     * @throws UsernameNotFoundException Si el usuario no existe en el sistema.
     */
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        try {
            Pair<UserDto, String> tupleUserDto = userService.findUserAndPasswordByEmail(usernameOrEmail)
                    .orElseThrow(() -> new IllegalArgumentException(
                            new MessageFormat("User with surname: {0} not found")
                                    .format(new Object[]{usernameOrEmail})
                    ));

            // Adaptar a UserDetails de Spring Security
            return toUserDetails(tupleUserDto.getLeft(), tupleUserDto.getRight());

        } catch (IllegalArgumentException e) {
            final String msg = new MessageFormat("user {0} not authorized, because: {1}")
                    .format(new Object[]{usernameOrEmail, e.getMessage()});
            log.error(msg, e);
            throw new UsernameNotFoundException(msg, e);
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
     * Obtiene las autoridades (roles, privilegios y alcances) del usuario.
     *
     * @param userDto Objeto que representa los datos del usuario.
     * @return Una lista de objetos {@link SimpleGrantedAuthority} que representan las autoridades del usuario.
     */
    private static List<SimpleGrantedAuthority> getAuthoritiesUser(UserDto userDto) {

        if (userDto == null || userDto.getRoles() == null) {
            return null;
        }

        // Convertir roles a SimpleGrantedAuthority, concatenando "ROLE_" para cumplir con los requisitos de Spring Security
        final List<SimpleGrantedAuthority> roles = Optional.of(userDto)
                .filter(userDto1 -> !userDto1.getRoles().isEmpty())
                .map(UserDto::getRoles)
                .stream()
                .flatMap(Collection::stream)
                .map(RoleDto::getName)
                .map(StringUtils::upperCase)
                .map("ROLE_"::concat)
                .map(SimpleGrantedAuthority::new)
                .toList();

        // Convertir privilegios a SimpleGrantedAuthority
        final List<SimpleGrantedAuthority> privileges = Optional.of(userDto)
                .filter(userDto1 -> !userDto1.getRoles().isEmpty())
                .map(UserDto::getRoles)
                .stream()
                .flatMap(Collection::stream)
                .map(RoleDto::getAuthorities)
                .filter(authorities -> authorities != null && !authorities.isEmpty())
                .flatMap(Collection::stream)
                .map(StringUtils::upperCase)
                .map(SimpleGrantedAuthority::new)
                .toList();

        // Convertir alcances a SimpleGrantedAuthority
        final List<SimpleGrantedAuthority> scopes = Optional.of(userDto)
                .filter(userDto1 -> !userDto1.getRoles().isEmpty())
                .map(UserDto::getRoles)
                .stream()
                .flatMap(Collection::stream)
                .map(RoleDto::getScopes)
                .filter(authorities -> authorities != null && !authorities.isEmpty())
                .flatMap(Collection::stream)
                .map(StringUtils::upperCase)
                .map("SCOPE_"::concat)
                .map(SimpleGrantedAuthority::new)
                .toList();

        // Combinar roles, privilegios y alcances en una sola lista
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.addAll(roles);
        authorities.addAll(privileges);
        authorities.addAll(scopes);

        return authorities;
    }
}