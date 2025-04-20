package com.tokioschool.filmweb.core;

import com.tokioschool.filmapp.dto.error.ErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

//@ControllerAdvice(annotations = Controller.class) // Punto para controlar las excepciones, solo
@ControllerAdvice // Controller + Rest controller
@Slf4j
public class ExceptionMvcHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView getExceptionHandler(Exception e, HttpServletRequest request){
        log.error("Error {}",e.getMessage(),e);
        final ErrorDto error = ErrorDto.builder()
                .url(request.getRequestURL().toString())
                .exception(e.getMessage()).build();

        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("error", error);
        modelAndView.setViewName("error");

        return modelAndView;
    }
}
