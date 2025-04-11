package com.tokioschool.ratingapp.core.advaice;

import com.tokioschool.core.exception.InternalErrorException;
import com.tokioschool.core.exception.NotFoundException;
import com.tokioschool.core.exception.OperationNotAllowException;
import com.tokioschool.core.exception.ValidacionException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
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
 * Manejador global de excepciones para controladores REST.
 *
 * Proporciona métodos para manejar diferentes tipos de excepciones y devolver respuestas HTTP adecuadas.
 * Incluye manejo de excepciones comunes como validaciones, errores de autenticación y errores internos del servidor.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@RestControllerAdvice(annotations = RestController.class)
@Slf4j
public class ExceptionRestAdvice {

    /**
     * Maneja NotFoundException y devuelve un estado 404 con un mensaje.
     *
     * @param ex la excepción NotFoundException
     * @param request el HttpServletRequest
     * @return un mapa que contiene el mensaje de error y la URI de la solicitud
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public Map<String, String> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        log.error("Error in %s".formatted(request.getRequestURI()), ex);
        return Map.of("message", ex.getMessage(), "request", request.getRequestURI());
    }

    /**
     * Maneja MethodArgumentNotValidException y devuelve un estado 400 con un mensaje.
     *
     * @param ex la excepción MethodArgumentNotValidException
     * @param request el HttpServletRequest
     * @return un mapa que contiene el mensaje de error
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(". "));
        log.error("Error in {}, messages: {}", request.getRequestURI(), message, ex);
        return Map.of("message", message);
    }

    /**
     * Maneja ConstraintViolationException y excepciones relacionadas, devolviendo un estado 400 con un mensaje.
     *
     * @param ex la excepción ConstraintViolationException, OperationNotAllowException o BadRequestException
     * @param request el HttpServletRequest
     * @return un mapa que contiene el mensaje de error y la URI de la solicitud
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            ConstraintViolationException.class,
            OperationNotAllowException.class,
            BadRequestException.class
    })
    public Map<String, String> handleConstraintViolationException(Exception ex, HttpServletRequest request) {
        log.error("Error in %s".formatted(request.getRequestURI()), ex);
        return Map.of("message", ex.getMessage(), "request", request.getRequestURI());
    }

    /**
     * Maneja ValidacionException y devuelve un estado 400 con un mensaje.
     *
     * @param ex la excepción ValidacionException
     * @param request el HttpServletRequest
     * @return un mapa que contiene los errores de validación
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidacionException.class)
    public Map<String, String> handlerErrorFormExceptionError(ValidacionException ex, HttpServletRequest request) {
        log.error("Error in %s".formatted(request.getRequestURI()), ex);
        return ex.getErrors();
    }

    /**
     * Maneja excepciones de autenticación y autorización, devolviendo un estado 401 con un mensaje.
     *
     * @param ex la excepción (BadCredentialsException, LoginException)
     * @param request el HttpServletRequest
     * @return un mapa que contiene el mensaje de error y la URI de la solicitud
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({
            BadCredentialsException.class,
            AccessDeniedException.class,
            LoginException.class
    })
    public Map<String, String> handleBadCredentialsExceptionError(Exception ex, HttpServletRequest request) {
        log.error("Error in %s".formatted(request.getRequestURI()), ex);
        return Map.of("message", ex.getMessage(), "request", request.getRequestURI());
    }

    /**
     * Maneja InternalErrorException y devuelve un estado 500 con un mensaje.
     *
     * @param ex la excepción InternalErrorException
     * @param request el HttpServletRequest
     * @return un mapa que contiene el mensaje de error y la URI de la solicitud
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalErrorException.class)
    public Map<String, String> handleInternalServerError(InternalErrorException ex, HttpServletRequest request) {
        log.error("Error in %s".formatted(request.getRequestURI()), ex);
        return Map.of("message", ex.getMessage(), "request", request.getRequestURI());
    }

    /**
     * Maneja excepciones genéricas y devuelve un estado 500 con un mensaje.
     *
     * @param ex la excepción
     * @param request el HttpServletRequest
     * @return un mapa que contiene el mensaje de error y la URI de la solicitud
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public Map<String, String> handleExceptionError(Exception ex, HttpServletRequest request) {
        log.error("Error in %s".formatted(request.getRequestURI()), ex);
        return Map.of("message", ex.getMessage(), "request", request.getRequestURI());
    }
}