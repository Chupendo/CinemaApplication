package com.tokioschool.filmapp.security;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service for auth
 *
 * @version 1.0
 * @author andres.rpenuela
 */
@Service
@RequiredArgsConstructor
public class FilmUserDetailsService implements UserDetailsService {



    /**
     * Method for authorized the user register in film
     *
     * @param username usernmae or email of user to authorized
     * @return an instance of user details de spring security
     *
     * @throws UsernameNotFoundException if the username don't exist in the system
     *
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
          // Todo
            return null;

        }catch (IllegalArgumentException e){
            throw new UsernameNotFoundException("Credenciales erróneas: {}".formatted(username),e);
        }
    }
}
