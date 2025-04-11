package com.tokioschool.filmapp.security.filter.auth;

import com.tokioschool.redis.services.JwtBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación JWT.
 *
 * Este filtro se ejecuta una vez por solicitud y se encarga de procesar el token JWT
 * incluido en la cabecera de autorización. Valida si el token está en la lista negra
 * y autentica al usuario si el token es válido.
 *
 * Anotaciones:
 * - {@link Component}: Marca esta clase como un componente de Spring para que sea detectada automáticamente.
 * - {@link RequiredArgsConstructor}: Genera un constructor con los argumentos requeridos para los campos finales.
 *
 * Dependencias:
 * - {@link JwtBlacklistService}: Servicio para verificar si un token JWT está en la lista negra.
 * - {@link AuthenticationManager}: Administrador de autenticación para procesar el token.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Servicio para verificar si un token JWT está en la lista negra.
     */
    private final JwtBlacklistService jwtBlacklistService;

    /**
     * Administrador de autenticación para procesar el token JWT.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Procesa la solicitud HTTP para autenticar al usuario basado en el token JWT.
     *
     * Este metodo extrae el token JWT de la cabecera de autorización, verifica si está
     * en la lista negra y, si es válido, autentica al usuario y establece el contexto
     * de seguridad.
     *
     * @param request Solicitud HTTP entrante.
     * @param response Respuesta HTTP saliente.
     * @param filterChain Cadena de filtros para continuar con el procesamiento.
     * @throws ServletException Si ocurre un error en el procesamiento del filtro.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extrae el token JWT de la cabecera de autorización
        String token = extractToken(request);

        // Verifica si el token está en la lista negra
        try {
            if (token != null && jwtBlacklistService.isBlacklisted(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token está en la lista negra");
                return;
            }
        }catch (RedisConnectionFailureException ex ){
            log.error("Error de conexión a Redis: {}", ex.getMessage(),ex);
        }

        // Si el token es válido, autentica al usuario
        if (token != null) {
            BearerTokenAuthenticationToken authenticationToken = new BearerTokenAuthenticationToken(token);
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Continúa con el siguiente filtro en la cadena
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT de la cabecera de autorización.
     *
     * Este metodo busca la cabecera "Authorization" en la solicitud HTTP y extrae
     * el token si comienza con el prefijo "Bearer ".
     *
     * @param request Solicitud HTTP entrante.
     * @return El token JWT si está presente, o {@code null} si no se encuentra.
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}