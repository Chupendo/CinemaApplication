package com.tokioschool.ratingapp.core.advaice;

import com.tokioschool.ratingapp.core.exceptions.InternalErrorException;
import com.tokioschool.ratingapp.core.exceptions.NotFoundException;
import com.tokioschool.ratingapp.core.exceptions.OperationNotAllowException;
import com.tokioschool.ratingapp.core.exceptions.ValidacionException;
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
 * Global exception handler for REST controllers.
 */
@RestControllerAdvice(annotations = RestController.class)
@Slf4j
public class ExceptionRestAdvice {

    /**
     * Handles NotFoundException and returns a 404 status with a message.
     *
     * @param ex the NotFoundException
     * @param request the HttpServletRequest
     * @return a map containing the error message and request URI
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public Map<String, String> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        return Map.of("message", ex.getMessage(),"request",request.getRequestURI());
    }

    /**
     * Handles MethodArgumentNotValidException and returns a 400 status with a message.
     *
     * @param ex the MethodArgumentNotValidException
     * @param request the HttpServletRequest
     * @return a map containing the error message
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message =
                ex.getBindingResult().getFieldErrors()
                        .stream()
                        .map(error -> error.getField()+": "+error.getDefaultMessage())
                        .collect(Collectors.joining(". "));
        return Map.of("message", message);
    }

    /**
     * Handles ConstraintViolationException and returns a 400 status with a message.
     *
     * @param ex the ConstraintViolationException, OperationNotAllowException, BadRequestException
     * @param request the HttpServletRequest
     * @return a map containing the error message and request URI
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            ConstraintViolationException.class,
            OperationNotAllowException.class,
            BadRequestException.class
    })
    public Map<String, String> handleConstraintViolationException(Exception ex, HttpServletRequest request) {
        return Map.of("message", ex.getMessage(),"request",request.getRequestURI());
    }

    /**
     * Handles ValidationException and returns a 400 status with a message.
     *
     * @param ex the ValidacionException
     * @param request the HttpServletRequest
     * @return a map containing the error message and request URI
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidacionException.class)
    public Map<String, String> handlerErrorFormExceptionError(ValidacionException ex, HttpServletRequest request) {
        return ex.getErrors();
    }

    /**
     * Handles authentication and authorization exceptions and returns a 401 status with a message.
     *
     * @param ex the exception (BadCredentialsException, LoginException)
     * @param request the HttpServletRequest
     * @return a map containing the error message and request URI
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({
            BadCredentialsException.class,
            AccessDeniedException.class,
            LoginException.class
    })
    public Map<String, String> handleBadCredentialsExceptionError(Exception ex, HttpServletRequest request) {
        return Map.of("message", ex.getMessage(),"request",request.getRequestURI());
    }

    /**
     * Handles InternalErrorException and returns a 500 status with a message.
     *
     * @param ex the InternalErrorException
     * @param request the HttpServletRequest
     * @return a map containing the error message and request URI
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalErrorException.class)
    public Map<String, String> handleInternalServerError(InternalErrorException ex, HttpServletRequest request) {
        return Map.of("message", ex.getMessage(),"request",request.getRequestURI());
    }

    /**
     * Handles generic exceptions and returns a 500 status with a message.
     *
     * @param ex the exception
     * @param request the HttpServletRequest
     * @return a map containing the error message and request URI
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public Map<String, String> handleExceptionError(Exception ex, HttpServletRequest request) {
        return Map.of("message", ex.getMessage(),"request",request.getRequestURI());
    }
}