package com.tokioschool.filmweb.controllers.mvc;

import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.records.SearchArtistRecord;
import com.tokioschool.filmapp.services.artist.ArtistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

/**
 * Controlador MVC para gestionar artistas.
 *
 * Este controlador maneja las operaciones relacionadas con la visualización, creación,
 * edición y registro de artistas en la aplicación web.
 *
 * Anotaciones utilizadas:
 * - `@Controller`: Marca esta clase como un controlador de Spring MVC.
 * - `@RequestMapping`: Define la ruta base para las solicitudes relacionadas con artistas.
 * - `@RequiredArgsConstructor`: Genera un constructor con los argumentos requeridos.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Controller
@RequestMapping("/web/artists")
@RequiredArgsConstructor
public class ArtistMvcController {

    /** Servicio para gestionar la lógica de negocio relacionada con artistas. */
    private final ArtistService artistService;

    /**
     * Maneja la visualización de la lista de artistas con paginación y búsqueda.
     *
     * @param page Número de página solicitado (por defecto 0).
     * @param pageSize Tamaño de la página (por defecto 10).
     * @param searchArtistRecord Objeto con los criterios de búsqueda.
     * @param model Modelo para pasar datos a la vista.
     * @return Nombre de la vista que muestra la lista de artistas.
     */
    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String listPageArtisHandler(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            @ModelAttribute("searchArtistRecord") SearchArtistRecord searchArtistRecord,
            Model model) {

        final PageDTO<ArtistDto> pageArtistDto = artistService.searchArtist(page, pageSize, searchArtistRecord);
        model.addAttribute("pageArtistDto", pageArtistDto);
        return "artists/list";
    }

    /**
     * Maneja la visualización del perfil de un artista.
     *
     * @param artisId ID del artista (opcional).
     * @param model Modelo para pasar datos a la vista.
     * @return Nombre de la vista que muestra el formulario del perfil del artista.
     */
    @GetMapping({"/profile/{artistId}"})
    @PreAuthorize("isAuthenticated()")
    public String profileHandler(@PathVariable(value = "artistId", required = false) Long artisId,
                                 Model model) {

        final ArtistDto artistDto = Optional.ofNullable(artisId)
                .map(artistService::findById)
                .orElseGet(() -> ArtistDto.builder().build());

        model.addAttribute("artist", artistDto);
        model.addAttribute("profileMode", Boolean.TRUE);
        model.addAttribute("isRegister", Boolean.FALSE);

        return "artists/form";
    }

    /**
     * Maneja la visualización del formulario para crear o editar un artista.
     *
     * @param artisId ID del artista (opcional).
     * @param model Modelo para pasar datos a la vista.
     * @return Nombre de la vista que muestra el formulario de creación/edición.
     */
    @GetMapping({"/form", "/form/", "/form/{artistId}"})
    @PreAuthorize("isAuthenticated()")
    public String artistCreateOrEditHandler(@PathVariable(value = "artistId", required = false) Long artisId,
                                            Model model) {
        final boolean isRegister = (artisId == null);
        final ArtistDto artistDto = Optional.ofNullable(artisId)
                .map(artistService::findById)
                .orElseGet(() -> ArtistDto.builder().build());

        if (!model.containsAttribute("artist")) {
            model.addAttribute("artist", artistDto);
        }

        model.addAttribute("isRegister", isRegister);
        model.addAttribute("profileMode", Boolean.FALSE);

        return "artists/form";
    }

    /**
     * Maneja el registro o la edición de un artista a través del formulario.
     *
     * @param artistId ID del artista (opcional).
     * @param artistDto DTO del artista con los datos enviados.
     * @param bindingResult Resultado de la validación del formulario.
     * @param redirectAttributes Atributos para redirección.
     * @param model Modelo para pasar datos a la vista.
     * @return Redirección a la lista de artistas o al formulario en caso de error.
     */
    @PostMapping({"/form", "/form/", "/form/{artistId}"})
    @PreAuthorize("isAuthenticated()")
    public RedirectView formRegisterOrEditArtist(
            @PathVariable(value = "artistId", required = false) Long artistId,
            @Valid @ModelAttribute(value = "artist") ArtistDto artistDto, BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("artist/form");
            modelAndView.addAllObjects(model.asMap());

            if (!model.containsAttribute("artist")) {
                modelAndView.addObject("artist", artistDto);
            }

            modelAndView.getModel().forEach(redirectAttributes::addFlashAttribute);

            final String maybeParam = Optional.ofNullable(artistId)
                    .map("/%s"::formatted)
                    .orElse(StringUtils.EMPTY);

            if (maybeParam.isEmpty()) {
                return new RedirectView("/web/artists/form");
            } else {
                return new RedirectView("/web/artists/form%s".formatted(maybeParam));
            }
        }

        if (artistId == null) {
            artistService.registerArtist(artistDto);
        } else {
            artistService.updatedArtist(artistId, artistDto);
        }

        return new RedirectView("/web/artists/list");
    }
}