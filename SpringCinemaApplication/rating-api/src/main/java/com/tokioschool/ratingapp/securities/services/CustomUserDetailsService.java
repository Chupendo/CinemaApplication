package com.tokioschool.ratingapp.securities.services;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Aquí recuperas el usuario de la base de datos, por ejemplo, usando un repositorio JPA
        if ("user".equals(username)) {
            return User.builder()
                    .username("user")
                    .password("$2y$10$5cTVuveCa8qhlZPApRF6kO5OwzhgF9nTDx57WuGKrb/E/E93Gw8Zq")//""{bcrypt}$2a$10$LDe7.j92o47ur5obHpGzIuUqVY41wVeTe5ZyF7/Tw9fdtJwbG1Dsu") // Contraseña encriptada
                    .roles("USER")
                    .build();
        }
        throw new UsernameNotFoundException("User not found");
    }
}