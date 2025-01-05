package com.tokioschool.filmapp.services.auth.impl;

import com.tokioschool.filmapp.dto.auth.AuthenticatedMeResponseDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationRequestDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationResponseDTO;
import com.tokioschool.filmapp.services.auth.AuthenticationService;
import com.tokioschool.filmapp.services.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {


    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationRequestDTO) {
        // auntentica el usuario en el sistema meidnate par [user, pwd]
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        authenticationRequestDTO.getUsername(),
                        authenticationRequestDTO.getPassword());

        // authentica al usuario usando el proveedor de spring
        Authentication authentication = authenticationManager
                .authenticate( usernamePasswordAuthenticationToken  );

        // se obtinete el usuario autenticado
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // generamos el token usando el servicio
        Jwt jwt = jwtService.generateToken(userDetails);

        // encapsula el token en uan resspeusta [token,expireIn]
        return AuthenticationResponseDTO.builder()
                .accessToken(jwt.getTokenValue())
                // +1 porque excluye el úlitmo digio del segunod y sea 3600 segundos
                .expiresIn(ChronoUnit.SECONDS.between(Instant.now(), jwt.getExpiresAt()) +1)
                .build();
    }

    @Override
    public AuthenticatedMeResponseDTO getAuthenticated() { // para saber quin hizo la petición
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

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

    @Override
    public Pair<String, Long> getTokenAndExpiredAt(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String tokenRequest = null;
        if (header != null && header.startsWith("Bearer ")) {
            tokenRequest = header.substring(7);
        }

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

}
