package com.tokioschool.storeapp.security.filter;

import com.tokioschool.storeapp.redis.service.RedisJwtBlackListService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro para verificar si un token JWT está en la lista negra antes de proceder con la autenticación.
 *
 * Este filtro se ejecuta una vez por solicitud y utiliza el servicio `RedisJwtBlackListService`
 * para comprobar si el token está en la lista negra. Si el token está en la lista negra, se
 * devuelve una respuesta de error y no se continúa con la cadena de filtros.
 *
 * Notas:
 * - Este filtro extiende `OncePerRequestFilter` para garantizar que se ejecute una vez por solicitud.
 * - Utiliza el encabezado `Authorization` para extraer el token JWT.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtBlackListFilter extends OncePerRequestFilter {

    private final RedisJwtBlackListService redisJwtBlackListService;

    /**
     * Verifica si el token de la solicitud está en la lista negra.
     *
     * Este metodo se ejecuta antes de la autenticación para comprobar si el token JWT
     * proporcionado en la solicitud está en la lista negra. Si el token está en la lista negra,
     * se devuelve una respuesta con un error 401 (No autorizado).
     *
     * @param request Solicitud HTTP que contiene el token a verificar.
     * @param response Respuesta HTTP que se enviará al cliente.
     * @param filterChain Cadena de filtros de seguridad de Spring.
     * @throws ServletException Si ocurre un error en el procesamiento del filtro.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.debug("Verificando si el token está presente en la lista negra");

        String token = extractToken(request);

        try {
            if (token != null && redisJwtBlackListService.isBlacklisted(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token está en la lista negra");
                return;
            }
        }catch (RedisConnectionFailureException ex ){
            log.error("Error de conexión a Redis: {}", ex.getMessage(),ex);
        }

        // Continuar con el filtro de autenticación
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT de la solicitud HTTP.
     *
     * Este metodo busca el encabezado `Authorization` en la solicitud y extrae el token JWT
     * eliminando el prefijo `Bearer `. Si el encabezado no está presente o no comienza con `Bearer `,
     * se devuelve `null`.
     *
     * @param request Solicitud HTTP que contiene el encabezado `Authorization`.
     * @return El token JWT sin el prefijo `Bearer `, o `null` si no está presente.
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}