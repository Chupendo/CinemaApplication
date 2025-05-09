package com.tokioschool.filmweb.controllers.mvc;

import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.movie.MovieDto;
import com.tokioschool.filmapp.services.movie.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador MVC para gestionar la página principal de la aplicación.
 *
 * Este controlador maneja las solicitudes relacionadas con la página de inicio
 * y la vista principal de la aplicación.
 *
 * Anotaciones utilizadas:
 * - `@Controller`: Marca esta clase como un controlador de Spring MVC.
 * - `@RequestMapping`: Define la ruta base para las solicitudes relacionadas con este controlador.
 * - `@RequiredArgsConstructor`: Genera un constructor con los argumentos requeridos.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Controller
@RequestMapping("/web")
@RequiredArgsConstructor
public class IndexMvcController {

    /** Servicio para gestionar la lógica de negocio relacionada con películas. */
    private final MovieService movieService;

    /**
     * Maneja las solicitudes a la raíz del controlador y redirige a la vista principal.
     *
     * @return Una redirección a la ruta "/web/index".
     */
    @GetMapping({"", "/"})
    public String home() {
        return "redirect:/web/index";  // Redirige a la vista index
    }

    /**
     * Maneja la visualización de la página principal de la aplicación.
     *
     * Este metodo obtiene una lista paginada de películas y la agrega al modelo
     * para ser utilizada en la vista.
     *
     * @param model El modelo utilizado para pasar datos a la vista.
     * @return El nombre de la vista principal ("index").
     */
    @GetMapping("/index")
    public String getIndexPageHandler(Model model) {
        PageDTO<MovieDto> movieDtoPageDTO = movieService.searchMovie();
        model.addAttribute("pageMovieDto", movieDtoPageDTO);
        return "index";
    }

}