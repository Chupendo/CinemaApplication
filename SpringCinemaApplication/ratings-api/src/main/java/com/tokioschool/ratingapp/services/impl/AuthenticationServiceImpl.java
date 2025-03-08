package com.tokioschool.ratingapp.services.impl;

import com.tokioschool.filmapp.dto.auth.AuthenticatedMeResponseDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationRequestDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationResponseDTO;
import com.tokioschool.ratingapp.dto.authorities.AuthorityDto;
import com.tokioschool.ratingapp.dto.roles.RoleDto;
import com.tokioschool.ratingapp.dto.users.UserDto;
import com.tokioschool.ratingapp.securities.jwt.service.JwtService;
import com.tokioschool.ratingapp.services.AuthenticationService;
import com.tokioschool.ratingapp.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.nio.file.AccessDeniedException;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    /**
     *
     * Method that authenticated a user in the system by user nad password
     *
     * @param authenticationRequestDTO username and password plain
     * @return token and expired at
     *
     * @throws UsernameNotFoundException the user don't found in the system
     * @throws BadCredentialsException if the password has error
     */
    @Override
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationRequestDTO) throws UsernameNotFoundException, BadCredentialsException, AccessDeniedException {
        final Pair<UserDto,String> pairUserAndPwd = userService.
                findUserAndPasswordByEmail(authenticationRequestDTO.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException( MessageFormat.format("User {0} don't found",authenticationRequestDTO.getUsername() ) ) );

        // Create Authentication Token, call by context to "RatingsUserDetailsService.loadUserByUsername(String):UserDetails"
        // where the param input to method is "AuthenticationResponseDTO::username"
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                authenticationRequestDTO.getUsername(),
                authenticationRequestDTO.getPassword(),
                loadAuthorities( pairUserAndPwd.getLeft() ) );

        // Authenticate User
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        // Generate JWT
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Jwt jwt = jwtService.generateToken(userDetails);

        // Set Security Context for maintain the security context for the current session
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Return JWT
        return AuthenticationResponseDTO.builder()
                .accessToken(jwt.getTokenValue())
                // +1 porque excluye el úlitmo digio del segundo y sea 3600 segundos
                .expiresIn(ChronoUnit.SECONDS.between(Instant.now(), jwt.getExpiresAt()) +1)
                .build();
    }

    /**
     * Get data about user with role 'ADMIN'
     *
     * @return data user authenticated
     * @throws LoginException if there is not authenticated
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public AuthenticatedMeResponseDTO getAuthenticated() throws LoginException { // para saber quin hizo la petición
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null){
            throw new LoginException("don't user authenticated");
        }

        return AuthenticatedMeResponseDTO.builder()
                .username(authentication.getName())
                .authorities(
                        Optional.of(authentication)
                                .map(Authentication::getAuthorities)
                                .filter(grantedAuthorities -> !grantedAuthorities.isEmpty())
                                .stream()
                                .flatMap(Collection::stream)
                                .map(GrantedAuthority::getAuthority)
                                .filter(value -> !value.toUpperCase().startsWith("ROLE_"))
                                .toList()

                )
                .roles(
                        Optional.of(authentication)
                                .map(Authentication::getAuthorities)
                                .filter(grantedAuthorities -> !grantedAuthorities.isEmpty())
                                .stream()
                                .flatMap(Collection::stream)
                                .map(GrantedAuthority::getAuthority)
                                .filter(value -> value.toUpperCase().startsWith("ROLE_"))
                                .toList()
                ).build();
    }


    /**
     * Method that token value and expired of request http
     *
     * @param request information of authentication in headers request http
     * @return token value and expired time of the same
     */
    @Override
    @PreAuthorize("isAuthenticated()")
    public Pair<String, Long> getTokenAndExpiredAt(HttpServletRequest request) {
        final String keyAuth = "Authorization";
        final String starJwtToken = "Bearer ";
        final String tokenRequest = Optional.ofNullable(request)
                .map(r -> r.getHeader(keyAuth))
                .filter(auth -> auth.startsWith(starJwtToken))
                .map(token -> token.substring(starJwtToken.length()))
                .orElseGet(() -> null );

        final Authentication authenticated = SecurityContextHolder.getContext().getAuthentication();

        String tokenAuthenticated = null;
        Instant expiresAt = null;

        if (authenticated != null && authenticated.getPrincipal() instanceof Jwt jwtToken) {
            tokenAuthenticated = jwtToken.getTokenValue();
            expiresAt =  jwtToken.getExpiresAt();
        }

        if(tokenRequest!=null && tokenAuthenticated!=null
                && Objects.equals(tokenRequest,tokenAuthenticated)
                &&  expiresAt != null){
            return Pair.of(tokenRequest,expiresAt.getEpochSecond());
        }

        return null;
    }



    /**
     * Load the loadAuthorities of user (privileges + roles)
     *
     * @param userDto data of user to authenticated
     * @return collection of authorities of user
     */
    private List<SimpleGrantedAuthority> loadAuthorities(@NonNull UserDto userDto) {
        List<SimpleGrantedAuthority> roles =  Optional.of(userDto)
                .filter(userDto1 -> userDto1.getRoles()!=null && !userDto1.getRoles().isEmpty() )
                .map(UserDto::getRoles)
                .stream()
                .flatMap(Collection::stream)
                .map(RoleDto::getName)
                .map(StringUtils::upperCase)
                .map(SimpleGrantedAuthority::new) // Mapea cada String a SimpleGrantedAuthority
                .toList();

        List<SimpleGrantedAuthority> privileges = Optional.of(userDto)
                .filter(userDto1 -> userDto1.getRoles()!=null && !userDto1.getRoles().isEmpty() )
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

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.addAll(privileges);
        authorities.addAll(roles);

        return authorities;
    }
}
