package com.tokioschool.filmapp.configs.interceptos;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

/**
 * Configuración de un interceptor para registrar las peticiones HTTP y medir el tiempo de ejecución.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
@Slf4j
public class LoggerConfiguration implements WebMvcConfigurer {

    /**
     * Define un interceptor para manejar las peticiones HTTP.
     * Este interceptor registra el tiempo de inicio y genera un identificador único para cada petición.
     *
     * @return una instancia de {@link HandlerInterceptor}.
     */
    @Bean
    public HandlerInterceptor handlerInterceptor() {
        return new HandlerInterceptor() {

            /**
             * Filtra las peticiones que comienzan con "/api".
             *
             * @param request la solicitud HTTP.
             * @return true si la URI comienza con "/api", de lo contrario false.
             */
            private boolean filter(HttpServletRequest request) {
                return request.getRequestURI().startsWith("/api");
            }

            /**
             * Metodo ejecutado antes de que la solicitud sea manejada por el controlador.
             * Registra el tiempo de inicio y genera un identificador único para la petición.
             *
             * @param request  la solicitud HTTP.
             * @param response la respuesta HTTP.
             * @param handler  el manejador de la solicitud.
             * @return true para continuar con el procesamiento de la solicitud.
             * @throws Exception si ocurre un error durante el manejo.
             */
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                if (filter(request)) {
                    request.setAttribute("ts", System.currentTimeMillis());
                    MDC.put("requestID", UUID.randomUUID().toString());
                    log.debug("request {} {} {}", request.getMethod(), request.getRequestURI(), MDC.get("requestID"));
                    findLoggerUser().ifPresent(userId -> MDC.put("user-ID", userId));
                }
                return HandlerInterceptor.super.preHandle(request, response, handler);
            }

            /**
             * Metodo ejecutado después de que el controlador maneje la solicitud, pero antes de generar la vista.
             * Calcula y registra el tiempo de ejecución de la solicitud.
             *
             * @param request      la solicitud HTTP.
             * @param response     la respuesta HTTP.
             * @param handler      el manejador de la solicitud.
             * @param modelAndView el modelo y la vista generados por el controlador.
             * @throws Exception si ocurre un error durante el manejo.
             */
            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
                if (filter(request)) {
                    long ts = (long) request.getAttribute("ts");
                    log.debug("response: {} {} ms: {} requestID: {}",
                            request.getMethod(),
                            request.getRequestURI(),
                            System.currentTimeMillis() - ts,
                            MDC.get("requestID"));
                }
                HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
            }

            /**
             * Metodo ejecutado después de que la solicitud haya sido completamente procesada.
             *
             * @param request  la solicitud HTTP.
             * @param response la respuesta HTTP.
             * @param handler  el manejador de la solicitud.
             * @param ex       excepción lanzada durante el manejo, si la hay.
             * @throws Exception si ocurre un error durante el manejo.
             */
            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
                HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
            }
        };
    }

    /**
     * Obtiene el usuario autenticado del contexto de seguridad.
     *
     * @return un {@link Optional} que contiene el nombre del usuario autenticado, o vacío si no hay autenticación.
     */
    private Optional<String> findLoggerUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Principal::getName);
    }

    /**
     * Registra el interceptor definido en el registro de interceptores de Spring.
     *
     * @param registry el registro de interceptores.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(handlerInterceptor());
    }
}