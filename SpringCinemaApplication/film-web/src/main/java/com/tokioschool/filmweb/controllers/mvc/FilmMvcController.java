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

@Controller
@RequestMapping("/web/films")
@RequiredArgsConstructor
@Slf4j
public class FilmMvcController {

    private final MovieService movieService;
    private final ArtistService artistService;
    private final UserService userService;
    private final StoreFacade storeFacade;
    private final RatingFacade ratingFacade;

    private final TranslatedMessageHelper translatedMessageHelper;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(ArtistDto.class, new ArtistDtoEditor(artistService));
    }

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String getFilmsHandler(
            @ModelAttribute("searchMovieRecord") SearchMovieRecord searchMovieRecord,
            Model model) {
        if (!model.containsAttribute("searchMovieRecord")) {
            searchMovieRecord = SearchMovieRecord.builder().page(0).pageSize(10).rangeReleaseYear(RangeReleaseYear.builder().build()).build();
            model.addAttribute("searchMovieRecord",searchMovieRecord );
        }

        if(searchMovieRecord.rangeReleaseYear() == null){
            searchMovieRecord = SearchMovieRecord.builder()
                    .title(searchMovieRecord.title())
                    .page(searchMovieRecord.page())
                    .pageSize(searchMovieRecord.pageSize())
                    .rangeReleaseYear(RangeReleaseYear.builder().build())
                    .build();
            model.addAttribute("searchMovieRecord",searchMovieRecord );

        }

        model.addAttribute("pageMovieDto", movieService.searchMovie(searchMovieRecord));
        return "movies/list";
    }

    @GetMapping("/detail/{movieId}")
    @PreAuthorize("isAuthenticated()")
    public String getMovieDetail(@PathVariable Long movieId) {
        return "forward:/web/films/form/%s?mode=%s".formatted(movieId,true); // nombre del archivo .html en templates/movies
    }

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

        if( profileMode ){
            final String userId = getUserIdAuth().getId();
            RatingFilmDto ratingFilmDto = ratingFacade.findRatingByUserIdAndMovieId(userId,movieId)
                            .orElseGet( () ->  RatingFilmDto.builder().filmId(movieId).userId( userId ).build());
            model.addAttribute("rating", ratingFilmDto );
            // todo añadir el average
            AverageRating averageRating = ratingFacade.findRatingAverageByMovieId(movieId)
                    .orElseGet(() -> AverageRating.builder().average(0.0).ratings(0L).build());
            model.addAttribute("averageRating", averageRating );
        }

        return "movies/form";
    }

    @PostMapping({"/form","/form/", "/form/{movieId}"})
    @PreAuthorize("isAuthenticated()")
    public RedirectView filmFormHandler(
            @PathVariable(name = "movieId", required = false) Long movieId,
            @Valid @ModelAttribute("movie") MovieDto movieDto,
            BindingResult bindingResult,
            @RequestParam(value = "file", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes
    ) {boolean isEdit = movieId != null;

        if (isEdit && !hasAdminRole()) {
            throw new AccessDeniedException("No tienes permisos para editar películas.");
        }


        // Validación imagen solo para creación o cambio de imagen
        if (!isEdit && (imageFile == null || imageFile.isEmpty())) {
            bindingResult.addError(new FieldError("movie", "resourceId",
                    translatedMessageHelper.getMessage("film.image.error.received")));
        }

        // Si hay errores
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.movie", bindingResult);
            redirectAttributes.addFlashAttribute("movie", movieDto);
            return new RedirectView("/web/films/form" + (isEdit ? "/" + movieId : ""));
        }

        // Subida y procesamiento de imagen si aplica
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
                final UserDto userDto = userService.findUserAuthenticated().orElseThrow( () -> new AuthenticationCredentialsNotFoundException("User not auth!"));
                movieDto.setCreateUser( userDto );
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

    private UserDto getUserIdAuth(){
        return userService.findUserAuthenticated().orElseThrow( () -> new AuthenticationCredentialsNotFoundException("User not auth!"));
    }

    @PostMapping("/rate")
    @PreAuthorize("isAuthenticated()")
    public String rateMovie(@Valid @ModelAttribute("rating") RatingFilmDto ratingFilmDto, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        // Aquí iría la lógica para guardar la puntuación
        // ratingService.save(ratingDto);
        if( bindingResult.hasErrors() ){
            Map<String,String> errorMsg = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> errorMsg.put(fieldError.getField(),fieldError.getDefaultMessage()));
            final String errorRate = translatedMessageHelper.getMessage("film.msg.rate.error");
            log.error(errorRate, errorMsg);
            redirectAttributes.addFlashAttribute("error", errorRate);
        }
        try{
            ratingFacade.registerRating(ratingFilmDto);
            final String successRate = translatedMessageHelper.getMessage("film.msg.rate.success");

            redirectAttributes.addFlashAttribute("message", successRate);

        }catch (Exception e){
            final String errorRate = translatedMessageHelper.getMessage("film.msg.rate.error");
            log.error(errorRate, e);
            redirectAttributes.addFlashAttribute("error", errorRate);
        }

        return "redirect:/web/films/detail/%s".formatted( ratingFilmDto.getFilmId() );
    }

    private boolean hasAdminRole() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
    }
}
