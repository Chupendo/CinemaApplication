package com.tokioschool.storeapp.service.impl;

import com.tokioschool.storeapp.dto.authentication.AuthenticatedMeResponseDTO;
import com.tokioschool.storeapp.dto.authentication.AuthenticationRequestDTO;
import com.tokioschool.storeapp.dto.authentication.AuthenticationResponseDTO;
import com.tokioschool.storeapp.security.jwt.service.JwtService;
import com.tokioschool.storeapp.service.AuthenticationService;
import com.tokioschool.storeapp.userdetails.dto.UserDto;
import com.tokioschool.storeapp.userdetails.service.StoreUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final StoreUserService storeUserService;
    private final PasswordEncoder passwordEncoder;

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
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationRequestDTO) throws UsernameNotFoundException, BadCredentialsException {
        final UserDto userDto = storeUserService.findByUserName(authenticationRequestDTO.getUsername());

        // Create Authentication Token
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                authenticationRequestDTO.getUsername(),
                authenticationRequestDTO.getPassword(),
                loadAuthorities(userDto) );
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
                        authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                                .filter(value -> !value.toUpperCase().startsWith("ROLE_"))
                                .toList()
                )
                .roles(
                        authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                                .filter(value -> value.toUpperCase().startsWith("ROLE_"))
                                .toList()
                ).build();
    }

    /**+
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
        String tokenRequest = Optional.ofNullable(request)
                .map(r -> r.getHeader(keyAuth))
                .filter(auth -> auth.startsWith(starJwtToken))
                .map(token -> token.substring(starJwtToken.length()))
                .orElseGet(() -> null );

        String tokenAuthenticated = null;
        Instant expiresAt = null;
        Authentication authenticated = SecurityContextHolder.getContext().getAuthentication();

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
     * Verify if the password of request is the password of user saved in the system
     *
     * @param rawPassword password to verify
     * @param encodedPassword password of user in the system
     * @return true if the password matches with password encoded
     */
    private boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword,encodedPassword.replace("{bcrypt}", StringUtils.EMPTY));
    }

    /**
     * Load the loadAuthorities of user (privileges + roles)
     *
     * @param userStore data of user to authenticated
     * @return collection of authorities of user
     */
    private List<SimpleGrantedAuthority> loadAuthorities(UserDto userStore) {
        List<SimpleGrantedAuthority> privileges = userStore.authorities().stream()
                .map(String::toUpperCase)
                .map(SimpleGrantedAuthority::new)
                .toList();
        List<SimpleGrantedAuthority> roles = userStore.roles().stream()
                .map(String::toUpperCase)
                .map("ROLE_"::concat)
                .map(SimpleGrantedAuthority::new)
                .toList();

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.addAll(privileges);
        authorities.addAll(roles);

        return authorities;
    }

}
