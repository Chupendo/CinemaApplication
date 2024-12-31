package com.tokioschool.filmapp.services.user.security;


import com.tokioschool.filmapp.dto.user.UserDTO;
import com.tokioschool.filmapp.services.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for auth
 *
 * @version 1.0
 * @author andres.rpenuela
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FilmUserDetailsService implements UserDetailsService {

    private final UserService userService;

    /**
     * Method for authorized the user register in film
     *
     * @param username username or email of user to authorized
     * @return an instance of user details de spring security
     *
     * @throws UsernameNotFoundException if the username don't exist in the system
     *
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Pair<UserDTO, String> tupleUserDto = userService.findUserAndPasswordByEmail(username)
                    .orElseThrow(() -> new IllegalArgumentException("User with surname: %s not found".formatted(username)));

            // se adapta a user details de spring
            return toUserDetails(tupleUserDto.getLeft(),tupleUserDto.getRight());


        }catch (IllegalArgumentException e){
            log.error("user {} not authorized, because: {} ",username,e.getMessage(),e);
            throw new UsernameNotFoundException("Credenciales erroneas: %s, because: {}".formatted(username),e);
        }
    }

    private UserDetails toUserDetails(UserDTO userDto,String password) {
        // Se crea la lista de autoridades a partir de los roles,
        // se puede considader un rol una autoridad, pero esto puede estar separado
        // List.of("ROLE_USER", "ROLE_ADMIN")

        final List<SimpleGrantedAuthority> simpleGrantedAuthorities = Optional.ofNullable(userDto.getRoles())
                .stream() // Convierte Optional<List<String>> en Stream<List<String>>
                .flatMap(List::stream) // Convierte Stream<List<String>> en Stream<String>
                .map(SimpleGrantedAuthority::new) // Mapea cada String a SimpleGrantedAuthority
                .toList(); // Convierte el Stream a una Lista



        return new org.springframework.security.core.userdetails.User(
                userDto.getEmail(), // identidad
                password, // credenciales (encriptada)
                simpleGrantedAuthorities// autoridades
        );
    }
}
