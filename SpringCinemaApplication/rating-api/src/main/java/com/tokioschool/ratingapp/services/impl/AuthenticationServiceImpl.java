package com.tokioschool.ratingapp.services.impl;

import com.nimbusds.jwt.SignedJWT;
import com.tokioschool.filmapp.dto.auth.AuthenticatedMeResponseDto;
import com.tokioschool.filmapp.dto.auth.AuthenticationRequestDto;
import com.tokioschool.filmapp.dto.auth.AuthenticationResponseDto;
import com.tokioschool.ratingapp.services.AuthenticationService;
import com.tokioschool.ratingapp.services.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Implementación del servicio de autenticación.
 *
 * Proporciona metodos para autenticar usuarios, obtener información del usuario autenticado
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
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Servicio para la generación y validación de tokens JWT.
     */
    @Qualifier("jwtOauth2Service")
    private final JwtService jwtService;

    /**
     * Autentica un usuario en el sistema.
     *
     * Este metodo valida las credenciales del usuario y genera un token JWT
     * que incluye información sobre el usuario autenticado.
     *
     * @param authenticationRequestDTO Objeto que contiene las credenciales del usuario.
     * @return Un objeto `AuthenticationResponseDto` con el token JWT y su tiempo de expiración.
     * @throws ParseException Si ocurre un error al analizar el token JWT.
     */
    @Override
    public AuthenticationResponseDto authenticate(AuthenticationRequestDto authenticationRequestDTO) throws ParseException {
        // Autentica al usuario en el sistema mediante [usuario, contraseña]
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        authenticationRequestDTO.getUsername(),
                        authenticationRequestDTO.getPassword());

        // Autentica al usuario usando el proveedor de Spring Security
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Obtiene el usuario autenticado
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Genera el token JWT usando el servicio
        SignedJWT signedJWT = jwtService.generateSignedJWT(userDetails);

        // Encapsula el token en una respuesta [token, tiempo de expiración]
        return AuthenticationResponseDto.builder()
                .accessToken(signedJWT.serialize())
                // +1 porque excluye el último dígito del segundo y sea 3600 segundos
                .expiresIn(ChronoUnit.SECONDS.between(Instant.now(), signedJWT.getJWTClaimsSet().getExpirationTime().toInstant()) + 1)
                .build();
    }

    /**
     * Obtiene información del usuario autenticado.
     *
     * Este metodo devuelve el nombre de usuario, roles y autoridades del usuario
     * que realizó la solicitud.
     *
     * @return Un objeto `AuthenticatedMeResponseDto` con la información del usuario autenticado.
     */
    @Override
    public AuthenticatedMeResponseDto getAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return AuthenticatedMeResponseDto.builder()
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

    /**
     * Obtiene el token JWT y su tiempo de expiración.
     *
     * Este metodo verifica si el token de la solicitud coincide con el token autenticado
     * y devuelve el token junto con su tiempo de expiración.
     *
     * @param request La solicitud HTTP que contiene el encabezado `Authorization`.
     * @return Un par que contiene el token JWT y su tiempo de expiración en segundos,
     *         o `null` si no se cumplen las condiciones.
     */
    @Override
    @PreAuthorize("isAuthenticated()")
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
                && expiresAt != null) {
            return Pair.of(tokenRequest, expiresAt.getEpochSecond());
        }
        return null;
    }
}