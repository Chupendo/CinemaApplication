package com.tokioschool.storeapp.core.advice;

import com.tokioschool.storeapp.core.exception.InternalErrorException;
import com.tokioschool.storeapp.core.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.login.LoginException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para los controladores REST.
 *
 * Esta clase utiliza la anotación {@link RestControllerAdvice} para interceptar y manejar
 * excepciones lanzadas por los controladores REST anotados con {@link RestController}.
 * Proporciona métodos para manejar diferentes tipos de excepciones y devolver respuestas
 * HTTP adecuadas con mensajes de error personalizados.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@RestControllerAdvice(annotations = RestController.class)
@Slf4j
public class ApiExceptionHandler {

    /**
     * Maneja excepciones de tipo {@link NotFoundException}.
     *
     * @param ex Excepción lanzada cuando no se encuentra un recurso.
     * @param request Objeto {@link HttpServletRequest} que contiene información de la solicitud.
     * @return Un mapa con el mensaje de error y la URI de la solicitud.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(com.tokioschool.storeapp.core.exception.NotFoundException.class)
    public Map<String, String> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        return Map.of("message", ex.getMessage(), "request", request.getRequestURI());
    }

    /**
     * Maneja excepciones de tipo {@link MethodArgumentNotValidException}.
     *
     * @param ex Excepción lanzada cuando los argumentos de un método no son válidos.
     * @param request Objeto {@link HttpServletRequest} que contiene información de la solicitud.
     * @return Un mapa con el mensaje de error y la URI de la solicitud.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message =
                ex.getBindingResult().getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                        .collect(Collectors.joining(". "));
        return Map.of("message", message);
    }

    /**
     * Maneja excepciones de tipo {@link ConstraintViolationException}.
     *
     * @param ex Excepción lanzada cuando se violan restricciones de validación.
     * @param request Objeto {@link HttpServletRequest} que contiene información de la solicitud.
     * @return Un mapa con el mensaje de error y la URI de la solicitud.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        return Map.of("message", ex.getMessage(), "request", request.getRequestURI());
    }

    /**
     * Maneja excepciones de tipo {@link BadCredentialsException}.
     *
     * @param ex Excepción lanzada cuando las credenciales proporcionadas son inválidas.
     * @param request Objeto {@link HttpServletRequest} que contiene información de la solicitud.
     * @return Un mapa con el mensaje de error y la URI de la solicitud.
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public Map<String, String> handleBadCredentialsExceptionError(BadCredentialsException ex, HttpServletRequest request) {
        return Map.of("message", ex.getMessage(), "request", request.getRequestURI());
    }

    /**
     * Maneja excepciones de tipo {@link LoginException}.
     *
     * @param ex Excepción lanzada cuando ocurre un error durante el inicio de sesión.
     * @param request Objeto {@link HttpServletRequest} que contiene información de la solicitud.
     * @return Un mapa con el mensaje de error y la URI de la solicitud.
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(LoginException.class)
    public Map<String, String> handleLoginExceptionError(LoginException ex, HttpServletRequest request) {
        return Map.of("message", ex.getMessage(), "request", request.getRequestURI());
    }

    /**
     * Maneja excepciones de tipo {@link AccessDeniedException}.
     *
     * @param ex Excepción lanzada cuando el acceso a un recurso es denegado.
     * @param request Objeto {@link HttpServletRequest} que contiene información de la solicitud.
     * @return Un mapa con el mensaje de error y la URI de la solicitud.
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AccessDeniedException.class)
    public Map<String, String> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        return Map.of("message", ex.getMessage(), "request", request.getRequestURI());
    }

    /**
     * Maneja excepciones de tipo {@link InternalErrorException}.
     *
     * @param ex Excepción lanzada cuando ocurre un error interno en el servidor.
     * @param request Objeto {@link HttpServletRequest} que contiene información de la solicitud.
     * @return Un mapa con el mensaje de error y la URI de la solicitud.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalErrorException.class)
    public Map<String, String> handleInternalServerError(InternalErrorException ex, HttpServletRequest request) {
        return Map.of("message", ex.getMessage(), "request", request.getRequestURI());
    }

    /**
     * Maneja excepciones genéricas de tipo {@link Exception}.
     *
     * @param ex Excepción genérica lanzada durante la ejecución.
     * @param request Objeto {@link HttpServletRequest} que contiene información de la solicitud.
     * @return Un mapa con el mensaje de error y la URI de la solicitud.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public Map<String, String> handleExceptionError(Exception ex, HttpServletRequest request) {
        log.error("Error interno del servidor", ex);
        return Map.of("message", ex.getMessage(), "request", request.getRequestURI());
    }
}