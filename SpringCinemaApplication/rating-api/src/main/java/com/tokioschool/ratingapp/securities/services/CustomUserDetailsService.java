package com.tokioschool.ratingapp.securities.services;

import com.tokioschool.ratingapp.dtos.RoleDto;
import com.tokioschool.ratingapp.dtos.UserDto;
import com.tokioschool.ratingapp.services.UserService;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

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

    private final UserService userService;

    /**
     * Loads the user details by username or email.
     *
     * @param usernameOrEmail the username or email of the user
     * @return UserDetails object containing user information
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        try {
            Pair<UserDto, String> tupleUserDto = userService.findUserAndPasswordByEmail(usernameOrEmail)
                    .orElseThrow(() -> new IllegalArgumentException( new MessageFormat("User with surname: {0} not found").format(new Object[]{usernameOrEmail}) ));

            // Adapt to Spring Security's UserDetails
            return toUserDetails(tupleUserDto.getLeft(),tupleUserDto.getRight());

        }catch (IllegalArgumentException e){
            final String msg = new MessageFormat("user {0} not authorized, because: {1}").format(new Object[]{usernameOrEmail,e.getMessage()});
            log.error(msg,e);
            throw new UsernameNotFoundException(msg,e);
        }
    }

    /**
     * Converts UserDto and password to UserDetails.
     *
     * @param userDto the user data transfer object
     * @param password the user's password
     * @return UserDetails object
     */
    private UserDetails toUserDetails(UserDto userDto, String password) {
        return new org.springframework.security.core.userdetails.User(
                userDto.getEmail(), // identity
                password, // credentials (encrypted)
                getAuthoritiesUser(userDto) // authorities
        );
    }

    /**
     * Retrieves the authorities (roles and privileges) of the user.
     *
     * @param userDto the user data transfer object
     * @return a list of SimpleGrantedAuthority objects
     */
    private static List<SimpleGrantedAuthority> getAuthoritiesUser(UserDto userDto) {

        if(userDto==null || userDto.getRoles() == null ){
            return null;
        }

        // Convert roles to SimpleGrantedAuthority, concat the "Role_" because insert as SimpleGrantedAuthority
        // and for read as ROLE at case, this is Required
        final List<SimpleGrantedAuthority> roles = Optional.of(userDto)
                .filter(userDto1 -> !userDto1.getRoles().isEmpty() )
                .map(UserDto::getRoles)
                .stream()
                .flatMap(Collection::stream)
                .map(RoleDto::getName)
                .map(StringUtils::upperCase)
                .map("ROLE_"::concat)
                .map(SimpleGrantedAuthority::new) // Mapea cada String a SimpleGrantedAuthority
                .toList();

        // Convert authorities to SimpleGrantedAuthority
        final List<SimpleGrantedAuthority> privileges =  Optional.of(userDto)
                .filter(userDto1 -> !userDto1.getRoles().isEmpty() )
                .map(UserDto::getRoles)
                .stream()
                .flatMap(Collection::stream)
                .map(RoleDto::getAuthorities)
                .filter(authorities -> authorities!=null && !authorities.isEmpty())
                .flatMap(Collection::stream)
                .map(StringUtils::upperCase)
                .map(SimpleGrantedAuthority::new) // Mapea cada String a SimpleGrantedAuthority
                .toList();

        // Convert authorities to SimpleGrantedAuthority
        final List<SimpleGrantedAuthority> scopes =  Optional.of(userDto)
                .filter(userDto1 -> !userDto1.getRoles().isEmpty() )
                .map(UserDto::getRoles)
                .stream()
                .flatMap(Collection::stream)
                .map(RoleDto::getScopes)
                .filter(authorities -> authorities!=null && !authorities.isEmpty())
                .flatMap(Collection::stream)
                .map(StringUtils::upperCase)
                .map("SCOPE_"::concat)
                .map(SimpleGrantedAuthority::new) // Mapea cada String a SimpleGrantedAuthority
                .toList();

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.addAll(roles);
        authorities.addAll(privileges);
        authorities.addAll(scopes);

        return authorities;
    }
}