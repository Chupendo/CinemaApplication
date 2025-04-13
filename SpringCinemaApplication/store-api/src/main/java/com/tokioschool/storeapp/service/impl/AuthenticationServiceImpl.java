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

/**
 * Implementación del servicio de autenticación.
 *
 * Esta clase proporciona métodos para autenticar usuarios, obtener información
 * del usuario autenticado y gestionar tokens JWT.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final StoreUserService storeUserService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Autentica a un usuario en el sistema utilizando su nombre de usuario y contraseña.
     *
     * @param authenticationRequestDTO Objeto que contiene el nombre de usuario y la contraseña en texto plano.
     * @return Un objeto `AuthenticationResponseDTO` que contiene el token JWT y su tiempo de expiración.
     * @throws UsernameNotFoundException Si el usuario no se encuentra en el sistema.
     * @throws BadCredentialsException Si la contraseña es incorrecta.
     */
    @Override
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationRequestDTO) throws UsernameNotFoundException, BadCredentialsException {
        final UserDto userDto = storeUserService.findByUserName(authenticationRequestDTO.getUsername());

        // Crear el token de autenticación
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                authenticationRequestDTO.getUsername(),
                authenticationRequestDTO.getPassword(),
                loadAuthorities(userDto) );
        // Autenticar al usuario
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        // Generar el token JWT
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Jwt jwt = jwtService.generateToken(userDetails);

        // Establecer el contexto de seguridad para mantener la sesión actual
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Retornar el token JWT
        return AuthenticationResponseDTO.builder()
                .accessToken(jwt.getTokenValue())
                // +1 porque excluye el último dígito del segundo y sea 3600 segundos
                .expiresIn(ChronoUnit.SECONDS.between(Instant.now(), jwt.getExpiresAt()) +1)
                .build();
    }

    /**
     * Obtiene los datos del usuario autenticado con el rol 'ADMIN'.
     *
     * @return Un objeto `AuthenticatedMeResponseDTO` con los datos del usuario autenticado.
     * @throws LoginException Si no hay un usuario autenticado.
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public AuthenticatedMeResponseDTO getAuthenticated() throws LoginException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null){
            throw new LoginException("No hay un usuario autenticado");
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

    /**
     * Obtiene el valor del token y su tiempo de expiración desde la solicitud HTTP.
     *
     * @param request La solicitud HTTP que contiene la información de autenticación en los encabezados.
     * @return Un par que contiene el valor del token y su tiempo de expiración en segundos.
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
     * Verifica si la contraseña proporcionada coincide con la contraseña codificada almacenada en el sistema.
     *
     * @param rawPassword La contraseña en texto plano que se desea verificar.
     * @param encodedPassword La contraseña codificada almacenada en el sistema.
     * @return `true` si las contraseñas coinciden, de lo contrario `false`.
     */
    private boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword,encodedPassword.replace("{bcrypt}", StringUtils.EMPTY));
    }

    /**
     * Carga las autoridades (privilegios y roles) de un usuario.
     *
     * @param userStore Los datos del usuario que se está autenticando.
     * @return Una lista de autoridades del usuario.
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