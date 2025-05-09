package com.tokioschool.filmapp.core.advice;

import com.tokioschool.core.exception.NotFoundException;
import com.tokioschool.core.exception.ValidacionException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.security.auth.login.LoginException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para controladores REST.
 *
 * Esta clase captura y maneja excepciones específicas lanzadas por los controladores REST,
 * devolviendo respuestas HTTP con códigos de estado y mensajes adecuados.
 *
 * Anotaciones:
 * - {@link RestControllerAdvice}: Indica que esta clase proporciona manejo global de excepciones para controladores REST.
 * - {@link Slf4j}: Proporciona un logger para registrar mensajes de error.
 *
 * Excepciones manejadas:
 * - {@link NotFoundException}
 * - {@link MethodArgumentNotValidException}
 * - {@link ConstraintViolationException}
 * - {@link AuthenticationCredentialsNotFoundException}
 * - {@link AuthorizationDeniedException}
 * - {@link AccessDeniedException}
 * - {@link LoginException}
 * - {@link BadCredentialsException}
 * - {@link ValidacionException}
 * - {@link BadRequestException}
 * - {@link MissingServletRequestPartException}
 * - {@link Exception}
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
     * @param ex Excepción lanzada.
     * @param request Objeto {@link HttpServletRequest} que contiene información de la solicitud.
     * @return Un mapa con el mensaje de error y la URI de la solicitud.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public Map<String, String> handlerNotFoundException(NotFoundException ex, HttpServletRequest request) {
        return Map.of("message", ex.getMessage(), "request", request.getRequestURI());
    }

    /**
     * Maneja excepciones de tipo {@link MethodArgumentNotValidException}.
     *
     * @param ex Excepción lanzada.
     * @param request Objeto {@link HttpServletRequest} que contiene información de la solicitud.
     * @return Un mapa con el mensaje de error generado a partir de los errores de validación.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
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
     * @param ex Excepción lanzada.
     * @param request Objeto {@link HttpServletRequest} que contiene información de la solicitud.
     * @return Un mapa con el mensaje de error y la URI de la solicitud.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> handlerConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        return Map.of("message", ex.getMessage(), "request", request.getRequestURI());
    }

    /**
     * Maneja excepciones relacionadas con problemas de autenticación y autorización.
     *
     * @param ex Excepción lanzada.
     * @param request Objeto {@link HttpServletRequest} que contiene información de la solicitud.
     * @return Un mapa con el mensaje de error y la URI de la solicitud.
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({
            AuthenticationCredentialsNotFoundException.class,
            AuthorizationDeniedException.class,
            AccessDeniedException.class,
            LoginException.class,
            BadCredentialsException.class
    })
    public Map<String, String> handlerAuthorizationDeniedExceptionError(Exception ex, HttpServletRequest request) {
        return Map.of("message", ex.getMessage(), "request", request.getRequestURI());
    }

    /**
     * Maneja excepciones de tipo {@link ValidacionException}.
     *
     * @param ex Excepción lanzada.
     * @param request Objeto {@link HttpServletRequest} que contiene información de la solicitud.
     * @return Un mapa con los errores de validación.
     */
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(ValidacionException.class)
    public Map<String, String> handlerErrorFormExceptionError(ValidacionException ex, HttpServletRequest request) {
        return ex.getErrors();
    }

    /**
     * Maneja excepciones de tipo {@link BadRequestException}.
     *
     * @param ex Excepción lanzada.
     * @param request Objeto {@link HttpServletRequest} que contiene información de la solicitud.
     * @return Un mapa con el mensaje de error y la URI de la solicitud.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public Map<String, String> handlerBadRequestExceptionError(BadRequestException ex, HttpServletRequest request) {
        return Map.of("message", ex.getMessage(), "request", request.getRequestURI());
    }

    /**
     * Maneja excepciones de tipo {@link MissingServletRequestPartException}.
     *
     * @param ex Excepción lanzada.
     * @param request Objeto {@link HttpServletRequest} que contiene información de la solicitud.
     * @return Un mapa con el mensaje de error y la URI de la solicitud.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestPartException.class)
    public Map<String, String> handlerBadRequestExceptionError(MissingServletRequestPartException ex, HttpServletRequest request) {
        return Map.of("message", ex.getMessage(), "request", request.getRequestURI());
    }

    /**
     * Maneja excepciones genéricas no controladas.
     *
     * @param ex Excepción lanzada.
     * @param request Objeto {@link HttpServletRequest} que contiene información de la solicitud.
     * @return Un mapa con el mensaje de error y la URI de la solicitud.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public Map<String, String> handlerInternalServerError(Exception ex, HttpServletRequest request) {
        log.error("Error %s".formatted(ex.getMessage()), ex);
        return Map.of("message", ex.getMessage(), "request", request.getRequestURI());
    }
}