package com.tokioschool.ratingapp.securities.services.userdetails;

import com.tokioschool.ratingapp.dto.authorities.AuthorityDto;
import com.tokioschool.ratingapp.dto.roles.RoleDto;
import com.tokioschool.ratingapp.dto.users.UserDto;
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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingsUserDetailsService implements UserDetailsService {

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

        // Convert roles to SimpleGrantedAuthority
        List<SimpleGrantedAuthority> roles = Optional.of(userDto)
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
        List<SimpleGrantedAuthority> privileges =  Optional.of(userDto)
                .filter(userDto1 -> !userDto1.getRoles().isEmpty() )
                .map(UserDto::getRoles)
                .stream()
                .flatMap(Collection::stream)
                .map(RoleDto::getAuthorities)
                .filter(authorities -> authorities!=null && !authorities.isEmpty())
                .flatMap(Collection::stream)
                .map(AuthorityDto::getName)
                .map(StringUtils::upperCase)
                .map(SimpleGrantedAuthority::new) // Mapea cada String a SimpleGrantedAuthority
                .toList();

        return Stream.concat(roles.stream(), privileges.stream())
                .toList();

    }
}
