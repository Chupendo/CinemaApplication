package com.tokioschool.filmweb.core;

import com.tokioschool.filmapp.dto.error.ErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * Manejador global de excepciones para controladores en la aplicación.
 *
 * Esta clase utiliza `@ControllerAdvice` para interceptar y manejar excepciones
 * lanzadas por los controladores de la aplicación, proporcionando una vista de error
 * personalizada.
 *
 * Anotaciones utilizadas:
 * - `@ControllerAdvice`: Permite manejar excepciones de forma global en los controladores.
 * - `@Slf4j`: Habilita el registro de logs utilizando Lombok.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@ControllerAdvice
@Slf4j
public class ExceptionMvcHandler {

    /**
     * Maneja excepciones genéricas lanzadas por los controladores.
     *
     * Este metodo captura cualquier excepción de tipo `Exception`, registra el error
     * en los logs y devuelve un objeto `ModelAndView` que contiene los detalles del error
     * y la vista de error correspondiente.
     *
     * @param e La excepción lanzada.
     * @param request La solicitud HTTP que generó la excepción.
     * @return Un objeto `ModelAndView` con los detalles del error y la vista "error".
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView getExceptionHandler(Exception e, HttpServletRequest request) {
        log.error("Error {}", e.getMessage(), e);

        // Construye un objeto ErrorDto con los detalles de la excepción y la URL de la solicitud.
        final ErrorDto error = ErrorDto.builder()
                .url(request.getRequestURL().toString())
                .exception(e.getMessage()).build();

        // Configura el ModelAndView con los datos del error y la vista "error".
        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("error", error);
        modelAndView.setViewName("error");

        return modelAndView;
    }
}