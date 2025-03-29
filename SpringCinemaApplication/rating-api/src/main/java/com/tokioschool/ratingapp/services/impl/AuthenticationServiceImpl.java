package com.tokioschool.ratingapp.services.impl;

import com.nimbusds.jwt.SignedJWT;
import com.tokioschool.ratingapp.dtos.*;
import com.tokioschool.ratingapp.jwt.servicies.JwtService;
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
import java.text.MessageFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationResponseDTO) throws Exception {
        final Pair<UserDto,String> pairUserAndPwd = userService.
                findUserAndPasswordByEmail(authenticationResponseDTO.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException( MessageFormat.format("User {0} don't found",authenticationResponseDTO.getUsername() ) ) );

        // Create Authentication Token, call by context to "RatingsUserDetailsService.loadUserByUsername(String):UserDetails"
        // where the param input to method is "AuthenticationResponseDTO::username"
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                authenticationResponseDTO.getUsername(),
                authenticationResponseDTO.getPassword(),
                loadAuthorities( pairUserAndPwd.getLeft() ) );

        // Authenticate User
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        // Generate JWT
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        SignedJWT jwt = jwtService.generateToken(userDetails);
        // JWKSource<SecurityContext> jwt =  new RatingsApiSecurityConfiguration().jwkSource();

        // Set Security Context for maintain the security context for the current session
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Return JWT
        return AuthenticationResponseDTO.builder()
                .accessToken(jwt.serialize())
                // +1 porque excluye el úlitmo digio del segundo y sea 3600 segundos
                .expiresIn( ChronoUnit.SECONDS.between(Instant.now(), ((Date) jwt.getJWTClaimsSet().getClaim("exp")).toInstant() ) +1  )//ChronoUnit.SECONDS.between(Instant.now(), jwt.getExpiresAt()) +1)
                .build();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public AuthenticatedMeResponseDTO getAuthenticated() throws LoginException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null){
            throw new LoginException("don't user authenticated");
        }

        return AuthenticatedMeResponseDTO.builder()
                .username(authentication.getName())
                .scopes(
                        Optional.of(authentication)
                                .map(Authentication::getPrincipal)
                                .filter(Jwt.class::isInstance)
                                .map(jwt -> (Jwt)jwt)
                                .map(token -> token.getClaim("scope"))
                                .filter(List.class::isInstance)
                                .map(scopes ->(List<String>)scopes)
                                .orElseGet(Collections::emptyList)
                )
                .authorities(
                        Optional.of(authentication)
                                .map(Authentication::getAuthorities)
                                .filter(grantedAuthorities -> !grantedAuthorities.isEmpty())
                                .stream()
                                .flatMap(Collection::stream)
                                .map(GrantedAuthority::getAuthority)
                                .filter(value -> !value.toUpperCase().startsWith("ROLE_") && !value.toUpperCase().startsWith("SCOPE_"))
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

    @Override
    public Pair<String, Long> getTokenAndExpiredAt(HttpServletRequest request) {
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
                .map("ROLE_"::concat)
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
                .map(StringUtils::upperCase)
                .map(SimpleGrantedAuthority::new) // Mapea cada String a SimpleGrantedAuthority
                .toList();

        List<SimpleGrantedAuthority> scopes = Optional.of(userDto)
                .filter(userDto1 -> userDto1.getRoles()!=null && !userDto1.getRoles().isEmpty() )
                .map(UserDto::getRoles)
                .stream()
                .flatMap(Collection::stream)
                .map(RoleDto::getScopes)
                .filter(authorities -> authorities!=null && !authorities.isEmpty())
                .flatMap(Collection::stream)
                .map("SCOPE_"::concat)
                .map(StringUtils::upperCase)
                .map(SimpleGrantedAuthority::new) // Mapea cada String a SimpleGrantedAuthority
                .toList();

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.addAll(roles);
        authorities.addAll(privileges);
        authorities.addAll(scopes);

        return authorities;
    }
}

