package com.tokioschool.filmweb.controllers.mvc;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador personalizado para manejar errores en la aplicación.
 *
 * Este controlador implementa la interfaz `ErrorController` de Spring Boot
 * para personalizar la gestión de errores y mostrar mensajes específicos
 * según el código de estado HTTP.
 *
 * Anotaciones utilizadas:
 * - `@Controller`: Marca esta clase como un controlador de Spring MVC.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Controller
public class CustomErrorController implements ErrorController {

    /**
     * Maneja las solicitudes a la ruta "/error".
     *
     * Este metodo obtiene el código de estado HTTP y la excepción original
     * (si existe) de la solicitud, y configura el modelo con un mensaje de error
     * adecuado para mostrar en la vista de error.
     *
     * @param request La solicitud HTTP que contiene los atributos del error.
     * @param model El modelo utilizado para pasar datos a la vista.
     * @return El nombre de la vista de error.
     */
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