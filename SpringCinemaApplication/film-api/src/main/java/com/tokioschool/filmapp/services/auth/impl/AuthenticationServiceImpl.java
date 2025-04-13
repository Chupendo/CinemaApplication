package com.tokioschool.filmapp.services.auth.impl;

import com.tokioschool.filmapp.dto.auth.AuthenticatedMeResponseDto;
import com.tokioschool.filmapp.dto.auth.AuthenticationRequestDto;
import com.tokioschool.filmapp.dto.auth.AuthenticationResponseDto;
import com.tokioschool.filmapp.services.auth.AuthenticationService;
import com.tokioschool.filmapp.services.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Implementación del servicio de autenticación.
 * Proporciona métodos para autenticar usuarios, obtener información del usuario autenticado
 * y gestionar tokens JWT.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    /**
     * Administrador de autenticación de Spring Security.
     * Se utiliza para autenticar usuarios mediante un token de autenticación.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Servicio para la generación y validación de tokens JWT.
     */
    @Qualifier("jwtServiceImpl")
    private final JwtService jwtService;

    /**
     * Autentica a un usuario en el sistema utilizando sus credenciales.
     *
     * @param authenticationRequestDTO Objeto que contiene el nombre de usuario y la contraseña.
     * @return Un objeto {@link AuthenticationResponseDto} que contiene el token de acceso y su tiempo de expiración.
     */
    @Override
    public AuthenticationResponseDto authenticate(AuthenticationRequestDto authenticationRequestDTO) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        authenticationRequestDTO.getUsername(),
                        authenticationRequestDTO.getPassword());

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Jwt jwt = jwtService.generateJwt(userDetails);

        return AuthenticationResponseDto.builder()
                .accessToken(jwt.getTokenValue())
                .expiresIn(ChronoUnit.SECONDS.between(Instant.now(), jwt.getExpiresAt()) + 1)
                .build();
    }

    /**
     * Obtiene información del usuario autenticado que realizó la solicitud.
     *
     * @return Un objeto {@link AuthenticatedMeResponseDto} que contiene el nombre de usuario,
     * roles, autoridades y alcances del usuario autenticado.
     */
    @Override
    public AuthenticatedMeResponseDto getAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return AuthenticatedMeResponseDto.builder()
                .username(authentication.getName())
                .authorities(
                        authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                                .filter(value -> !value.toUpperCase().startsWith("ROLE_") && !value.toUpperCase().startsWith("SCOPE_"))
                                .toList()
                )
                .roles(
                        authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                                .filter(value -> value.toUpperCase().startsWith("ROLE_"))
                                .toList()
                )
                .scopes(
                        authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                                .filter(value -> value.toUpperCase().startsWith("SCOPE_"))
                                .toList()
                ).build();
    }

    /**
     * Obtiene el token JWT y su tiempo de expiración de la solicitud HTTP.
     *
     * @param request La solicitud HTTP que contiene el encabezado de autorización.
     * @return Un par que contiene el token JWT y su tiempo de expiración en segundos, o {@code null} si no se cumplen las condiciones.
     */
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
            expiresAt = jwtToken.getExpiresAt();
        }

        if (tokenRequest != null && tokenAuthenticated != null
                && Objects.equals(tokenRequest, tokenAuthenticated)
                && expiresAt != null && expiresAt.isAfter(Instant.now())) {
            return Pair.of(tokenRequest, expiresAt.getEpochSecond());
        }
        return null;
    }
}