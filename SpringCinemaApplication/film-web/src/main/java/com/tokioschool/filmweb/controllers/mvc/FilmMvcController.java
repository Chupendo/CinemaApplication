package com.tokioschool.filmweb.controllers.mvc;

import com.tokioschool.core.exception.OperationNotAllowException;
import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.dto.movie.MovieDto;
import com.tokioschool.filmapp.dto.ratings.RatingFilmDto;
import com.tokioschool.filmapp.dto.user.UserDto;
import com.tokioschool.filmapp.enums.TYPE_ARTIST;
import com.tokioschool.filmapp.records.AverageRating;
import com.tokioschool.filmapp.records.RangeReleaseYear;
import com.tokioschool.filmapp.records.SearchMovieRecord;
import com.tokioschool.filmapp.services.artist.ArtistService;
import com.tokioschool.filmapp.services.movie.MovieService;
import com.tokioschool.filmapp.services.user.UserService;
import com.tokioschool.filmweb.core.propertyEditors.ArtistDtoEditor;
import com.tokioschool.filmweb.helpers.TranslatedMessageHelper;
import com.tokioschool.ratings.facade.RatingFacade;
import com.tokioschool.store.dto.ResourceIdDto;
import com.tokioschool.store.facade.StoreFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;

/**
 * Controlador MVC para gestionar películas.
 *
 * Este controlador maneja las operaciones relacionadas con la visualización, creación,
 * edición, calificación y detalles de películas en la aplicación web.
 *
 * Anotaciones utilizadas:
 * - `@Controller`: Marca esta clase como un controlador de Spring MVC.
 * - `@RequestMapping`: Define la ruta base para las solicitudes relacionadas con películas.
 * - `@RequiredArgsConstructor`: Genera un constructor con los argumentos requeridos.
 * - `@Slf4j`: Habilita el registro de logs utilizando Lombok.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Controller
@RequestMapping("/web/films")
@RequiredArgsConstructor
@Slf4j
public class FilmMvcController {

    /** Servicio para gestionar la lógica de negocio relacionada con películas. */
    private final MovieService movieService;

    /** Servicio para gestionar la lógica de negocio relacionada con artistas. */
    private final ArtistService artistService;

    /** Servicio para gestionar la lógica de negocio relacionada con usuarios. */
    private final UserService userService;

    /** Fachada para gestionar recursos en el almacenamiento. */
    private final StoreFacade storeFacade;

    /** Fachada para gestionar calificaciones de películas. */
    private final RatingFacade ratingFacade;

    /** Ayudante para traducir mensajes en la aplicación. */
    private final TranslatedMessageHelper translatedMessageHelper;

    /**
     * Inicializa el `WebDataBinder` para registrar editores personalizados.
     *
     * @param binder El `WebDataBinder` utilizado para registrar editores personalizados.
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(ArtistDto.class, new ArtistDtoEditor(artistService));
    }

    /**
     * Maneja la visualización de la lista de películas con paginación y búsqueda.
     *
     * @param searchMovieRecord Objeto con los criterios de búsqueda.
     * @param model Modelo para pasar datos a la vista.
     * @return Nombre de la vista que muestra la lista de películas.
     */
    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String getFilmsHandler(
            @ModelAttribute("searchMovieRecord") SearchMovieRecord searchMovieRecord,
            Model model) {
        if (!model.containsAttribute("searchMovieRecord")) {
            searchMovieRecord = SearchMovieRecord.builder().page(0).pageSize(10).rangeReleaseYear(RangeReleaseYear.builder().build()).build();
            model.addAttribute("searchMovieRecord", searchMovieRecord);
        }

        if (searchMovieRecord.rangeReleaseYear() == null) {
            searchMovieRecord = SearchMovieRecord.builder()
                    .title(searchMovieRecord.title())
                    .page(searchMovieRecord.page())
                    .pageSize(searchMovieRecord.pageSize())
                    .rangeReleaseYear(RangeReleaseYear.builder().build())
                    .build();
            model.addAttribute("searchMovieRecord", searchMovieRecord);
        }

        model.addAttribute("pageMovieDto", movieService.searchMovie(searchMovieRecord));
        return "movies/list";
    }

    /**
     * Redirige a la vista de detalles de una película.
     *
     * @param movieId ID de la película.
     * @return Redirección a la vista de detalles de la película.
     */
    @GetMapping("/detail/{movieId}")
    @PreAuthorize("isAuthenticated()")
    public String getMovieDetail(@PathVariable Long movieId) {
        return "forward:/web/films/form/%s?mode=%s".formatted(movieId, true);
    }

    /**
     * Maneja la visualización del formulario para crear o editar una película.
     *
     * @param movieId ID de la película (opcional).
     * @param profileMode Indica si el formulario está en modo de perfil.
     * @param model Modelo para pasar datos a la vista.
     * @return Nombre de la vista del formulario de creación/edición.
     */
    @GetMapping({"/form", "/form/{movieId}"})
    public String filmCreateOrEditHandler(@PathVariable(name = "movieId", required = false) Long movieId,
                                          @RequestParam(value = "mode", defaultValue = "false") boolean profileMode,
                                          Model model) {
        boolean isEdit = movieId != null;

        if (isEdit && !profileMode && !hasAdminRole()) {
            throw new AccessDeniedException("No tienes permisos para editar películas.");
        }

        MovieDto movieDto = Optional.ofNullable(movieId)
                .map(movieService::getMovieById)
                .orElseGet(MovieDto::new);

        if (!model.containsAttribute("movie")) {
            model.addAttribute("movie", movieDto);
        }

        List<ArtistDto> managers = artistService.findByAllByTypeArtist(TYPE_ARTIST.DIRECTOR);
        List<ArtistDto> actors = artistService.findByAllByTypeArtist(TYPE_ARTIST.ACTOR);

        if (managers.isEmpty() || actors.isEmpty()) {
            throw new OperationNotAllowException(translatedMessageHelper.getMessage("movie.artists.not.available"));
        }

        model.addAttribute("managers", managers);
        model.addAttribute("actors", actors);
        model.addAttribute("profileMode", profileMode);

        if (profileMode) {
            final String userId = getUserIdAuth().getId();
            RatingFilmDto ratingFilmDto = ratingFacade.findRatingByUserIdAndMovieId(userId, movieId)
                    .orElseGet(() -> RatingFilmDto.builder().filmId(movieId).userId(userId).build());
            model.addAttribute("rating", ratingFilmDto);

            AverageRating averageRating = ratingFacade.findRatingAverageByMovieId(movieId)
                    .orElseGet(() -> AverageRating.builder().average(0.0).ratings(0L).build());
            model.addAttribute("averageRating", averageRating);
        }

        return "movies/form";
    }

    /**
     * Maneja el registro o la edición de una película a través del formulario.
     *
     * @param movieId ID de la película (opcional).
     * @param movieDto DTO de la película con los datos enviados.
     * @param bindingResult Resultado de la validación del formulario.
     * @param imageFile Archivo de imagen enviado (opcional).
     * @param redirectAttributes Atributos para redirección.
     * @return Redirección a la lista de películas o al formulario en caso de error.
     */
    @PostMapping({"/form", "/form/", "/form/{movieId}"})
    @PreAuthorize("isAuthenticated()")
    public RedirectView filmFormHandler(
            @PathVariable(name = "movieId", required = false) Long movieId,
            @Valid @ModelAttribute("movie") MovieDto movieDto,
            BindingResult bindingResult,
            @RequestParam(value = "file", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes
    ) {
        boolean isEdit = movieId != null;

        if (isEdit && !hasAdminRole()) {
            throw new AccessDeniedException("No tienes permisos para editar películas.");
        }

        if (!isEdit && (imageFile == null || imageFile.isEmpty())) {
            bindingResult.addError(new FieldError("movie", "resourceId",
                    translatedMessageHelper.getMessage("film.image.error.received")));
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.movie", bindingResult);
            redirectAttributes.addFlashAttribute("movie", movieDto);
            return new RedirectView("/web/films/form" + (isEdit ? "/" + movieId : ""));
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            Optional<ResourceIdDto> resourceIdDtoOptional = storeFacade.saveResource(imageFile, null);
            if (resourceIdDtoOptional.isEmpty()) {
                bindingResult.addError(new FieldError("movie", "resourceId",
                        translatedMessageHelper.getMessage("film.image.error.save")));

                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.movie", bindingResult);
                redirectAttributes.addFlashAttribute("movie", movieDto);
                return new RedirectView("/web/films/form" + (isEdit ? "/" + movieId : ""));
            }

            if (isEdit && movieDto.getResourceId() != null) {
                Optional.of(movieDto.getResourceId()).map(UUID::fromString).ifPresent(storeFacade::deleteResource);
            }

            movieDto.setResourceId(resourceIdDtoOptional.get().resourceId().toString());
        }

        try {
            if (isEdit) {
                movieService.updateMovie(movieId, movieDto);
            } else {
                final UserDto userDto = userService.findUserAuthenticated().orElseThrow(() -> new AuthenticationCredentialsNotFoundException("User not auth!"));
                movieDto.setCreateUser(userDto);
                movieService.createMovie(movieDto);
            }
        } catch (Exception e) {
            log.error("Error al guardar la película: {}", e.getMessage(), e);
            Optional.ofNullable(movieDto.getResourceId())
                    .map(UUID::fromString)
                    .ifPresent(storeFacade::deleteResource);
            throw e;
        }

        return new RedirectView("/web/films/list");
    }

    /**
     * Obtiene el usuario autenticado actual.
     *
     * @return DTO del usuario autenticado.
     */
    private UserDto getUserIdAuth() {
        return userService.findUserAuthenticated().orElseThrow(() -> new AuthenticationCredentialsNotFoundException("User not auth!"));
    }

    /**
     * Maneja la calificación de una película.
     *
     * @param ratingFilmDto DTO con los datos de la calificación.
     * @param bindingResult Resultado de la validación del formulario.
     * @param redirectAttributes Atributos para redirección.
     * @return Redirección a la vista de detalles de la película.
     */
    @PostMapping("/rate")
    @PreAuthorize("isAuthenticated()")
    public String rateMovie(@Valid @ModelAttribute("rating") RatingFilmDto ratingFilmDto, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMsg = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> errorMsg.put(fieldError.getField(), fieldError.getDefaultMessage()));
            final String errorRate = translatedMessageHelper.getMessage("film.msg.rate.error");
            log.error(errorRate, errorMsg);
            redirectAttributes.addFlashAttribute("error", errorRate);
        }
        try {
            ratingFacade.registerRating(ratingFilmDto);
            final String successRate = translatedMessageHelper.getMessage("film.msg.rate.success");

            redirectAttributes.addFlashAttribute("message", successRate);

        } catch (Exception e) {
            final String errorRate = translatedMessageHelper.getMessage("film.msg.rate.error");
            log.error(errorRate, e);
            redirectAttributes.addFlashAttribute("error", errorRate);
        }

        return "redirect:/web/films/detail/%s".formatted(ratingFilmDto.getFilmId());
    }

    /**
     * Verifica si el usuario autenticado tiene el rol de administrador.
     *
     * @return `true` si el usuario tiene el rol de administrador, de lo contrario `false`.
     */
    private boolean hasAdminRole() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
    }
}