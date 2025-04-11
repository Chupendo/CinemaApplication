package com.tokioschool.ratingapp.securities.filters;

import com.tokioschool.filmapp.services.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Filtro para registrar información de las solicitudes HTTP.
 *
 * Este filtro se ejecuta una vez por solicitud y registra información como el tiempo de ejecución,
 * el metodo HTTP, el estado de la respuesta y la ruta solicitada. También actualiza el último tiempo
 * de inicio de sesión del usuario autenticado en ciertas rutas específicas.
 *
 * Anotaciones:
 * - {@link Component}: Marca esta clase como un componente de Spring para su detección automática.
 * - {@link Slf4j}: Proporciona un logger para registrar mensajes.
 * - {@link Order}: Define la prioridad más baja para este filtro, asegurando que se ejecute al final.
 * - {@link RequiredArgsConstructor}: Genera un constructor con los argumentos requeridos para los campos finales.
 *
 * Dependencias:
 * - {@link UserService}: Servicio para manejar operaciones relacionadas con usuarios.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Component
@Slf4j
@Order(value = Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
public class LogRequestFilter extends OncePerRequestFilter {

    /**
     * Servicio para manejar operaciones relacionadas con usuarios.
     */
    private final UserService userService;

    /**
     * Procesa la solicitud HTTP y registra información relevante.
     *
     * Este metodo mide el tiempo de ejecución de la solicitud, registra información del usuario autenticado
     * y actualiza el último tiempo de inicio de sesión si la solicitud es exitosa y corresponde a rutas específicas.
     *
     * @param request Solicitud HTTP entrante.
     * @param response Respuesta HTTP saliente.
     * @param filterChain Cadena de filtros para continuar con el procesamiento.
     * @throws ServletException Si ocurre un error en el procesamiento del filtro.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long startTimeMs = System.currentTimeMillis();

        // Registra información del usuario autenticado
        considerSecurity();

        try {
            // Continúa con el procesamiento de la solicitud
            filterChain.doFilter(request, response);

            // Actualiza el último tiempo de inicio de sesión si la solicitud es exitosa y corresponde a rutas específicas
            if (response.getStatus() == 200 &&
                    (getRequestPath(request).equals("/api/ratings/login") ||
                            getRequestPath(request).contains("/login/oauth2/code") ) ) {
                userService.updateLastLoginTime();
            }

        } finally {
            // Registra el tiempo de ejecución de la solicitud
            long endTimeMs = System.currentTimeMillis() - startTimeMs;
            log.info("Request: time.ms: {}, method: {}, status: {}, path: {}",
                    endTimeMs, request.getMethod(), response.getStatus(), getRequestPath(request));
        }
    }

    /**
     * Registra información del usuario autenticado en el contexto de seguridad.
     *
     * Si hay un usuario autenticado, su ID se agrega al contexto de registro (MDC).
     */
    protected void considerSecurity() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            org.slf4j.MDC.put("user-ID", SecurityContextHolder.getContext().getAuthentication().getName());
        }
    }

    /**
     * Obtiene la ruta de la solicitud HTTP.
     *
     * Este metodo construye la ruta completa de la solicitud, incluyendo los parámetros de consulta si están presentes.
     *
     * @param request Solicitud HTTP entrante.
     * @return La ruta completa de la solicitud.
     */
    private String getRequestPath(final HttpServletRequest request) {
        final StringBuilder requestURI = new StringBuilder(request.getRequestURI());
        // Agrega los parámetros de consulta si están presentes
        Optional.ofNullable(request.getQueryString())
                .ifPresent(qs -> requestURI.append("?").append(qs));

        return requestURI.toString();
    }
}