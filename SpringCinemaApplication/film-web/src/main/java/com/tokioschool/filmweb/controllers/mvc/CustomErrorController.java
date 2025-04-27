package com.tokioschool.filmweb.controllers.mvc;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Obtener código de error
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        // Obtener excepción original
        Throwable throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            model.addAttribute("statusCode", statusCode);

            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("errorMessage", "Acceso denegado (403)");
            } else if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("errorMessage", "Página no encontrada (404)");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                // Aquí puedes mostrar o loguear la excepción
                if (throwable != null) {
                    model.addAttribute("errorMessage", "Error interno del servidor (500): " + throwable.getMessage());
                } else {
                    model.addAttribute("errorMessage", "Error interno del servidor (500)");
                }
            } else {
                model.addAttribute("errorMessage", "Ha ocurrido un error desconocido.");
            }
        }
        return "error";
    }
}
