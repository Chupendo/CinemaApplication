package com.tokioschool.filmweb.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador MVC para gestionar las operaciones relacionadas con el inicio de sesión.
 *
 * Este controlador maneja las solicitudes para redirigir a la página principal,
 * mostrar la página de inicio de sesión y gestionar el cierre de sesión.
 *
 * Anotaciones utilizadas:
 * - `@Controller`: Marca esta clase como un controlador de Spring MVC.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Controller
public class LoginMvcController {

    /**
     * Maneja las solicitudes a la raíz del controlador y redirige a la página principal.
     *
     * @return Una redirección a la ruta "/web?lang=es".
     */
    @GetMapping({"", "/"})
    public String homeHandler() {
        return "redirect:/web?lang=es";  // Redirige a la vista index
    }

    /**
     * Maneja las solicitudes para mostrar la página de inicio de sesión.
     *
     * @return El nombre de la vista de inicio de sesión ("login").
     */
    @GetMapping("/login")
    public String loginHandler() {
        return "login";
    }

    /**
     * Maneja las solicitudes para mostrar la página de cierre de sesión.
     *
     * @return El nombre de la vista de cierre de sesión ("logout").
     */
    @GetMapping("/logout")
    public String logoutHandler() {
        return "logout";
    }
}