package com.tokioschool.filmweb.controllers.mvc;

import com.tokioschool.core.exception.OperationNotAllowException;
import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.user.UserDto;
import com.tokioschool.filmapp.dto.user.UserFormDto;
import com.tokioschool.filmapp.enums.RoleEnum;
import com.tokioschool.filmapp.records.SearchUserRecord;
import com.tokioschool.filmapp.services.user.UserService;
import com.tokioschool.helpers.UUIDHelper;
import com.tokioschool.store.dto.ResourceIdDto;
import com.tokioschool.store.facade.StoreFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador MVC para gestionar usuarios.
 *
 * Este controlador maneja las operaciones relacionadas con la visualización, creación,
 * edición, registro y listado de usuarios en la aplicación web.
 *
 * Anotaciones utilizadas:
 * - `@Controller`: Marca esta clase como un controlador de Spring MVC.
 * - `@RequestMapping`: Define la ruta base para las solicitudes relacionadas con usuarios.
 * - `@RequiredArgsConstructor`: Genera un constructor con los argumentos requeridos.
 * - `@SessionAttributes`: Indica que el atributo "user" se almacena en la sesión.
 *
 * @author andres.repnuela
 * @version 1.0
 */
@Controller
@RequestMapping("/web/users")
@RequiredArgsConstructor
@SessionAttributes({"user"})
public class UserMvcController {

    /** Fachada para gestionar recursos en el almacenamiento. */
    private final StoreFacade storeFacade;

    /** Servicio para gestionar la lógica de negocio relacionada con usuarios. */
    private final UserService userService;

    /** Mapper para convertir entre entidades y DTOs. */
    private final ModelMapper modelMapper;

    /** Directorio de subida de imágenes. */
    private final String UPLOAD_DIR = "src/main/resources/static/images/";

    /**
     * Maneja la visualización del perfil de un usuario.
     *
     * @param userId ID del usuario (opcional).
     * @param model Modelo para pasar datos a la vista.
     * @return Redirección interna a la vista de edición del usuario.
     */
    @GetMapping({"/profile", "/profile/{userId}"})
    @PreAuthorize("isAuthenticated()")
    public String profileHandler(@PathVariable(name = "userId", required = false) String userId,
                                 Model model) {
        UserFormDto userFormDto;
        if (userId != null) {
            userFormDto = Optional.of(userId)
                    .map(userService::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(userDto -> modelMapper.map(userDto, UserFormDto.class))
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado."));
        } else {
            userFormDto = userService.findUserAuthenticated()
                    .map(userDto -> modelMapper.map(userDto, UserFormDto.class))
                    .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Usuario no autenticado"));
        }

        model.addAttribute("user", userFormDto);
        return ("forward:/web/users/edit/%s?mode=%s".formatted(userFormDto.getId(), true));
    }

    /**
     * Maneja la visualización del formulario para crear o editar un usuario.
     *
     * @param userId ID del usuario (opcional).
     * @param profileMode Indica si el formulario está en modo de perfil.
     * @param model Modelo para pasar datos a la vista.
     * @return Modelo y vista con los datos del usuario y la vista correspondiente.
     */
    @GetMapping({"/register", "/edit/{userId}"})
    public ModelAndView userCreateOrEditHandler(@PathVariable(name = "userId", required = false) String userId,
                                                @RequestParam(value = "mode", defaultValue = "false", required = false) boolean profileMode,
                                                Model model) {
        final ModelAndView modelAndView = new ModelAndView();
        if (Objects.nonNull(userId) && !userService.operationEditAllow(userId)) {
            throw new OperationNotAllowException("Operation not allow");
        }

        final UserFormDto userFormDto = Optional.ofNullable(userId)
                .map(userService::findById)
                .map(dto -> modelMapper.map(dto, UserFormDto.class))
                .orElse(UserFormDto.builder().build());

        modelAndView.addAllObjects(model.asMap());

        if (!model.containsAttribute("user")) {
            modelAndView.addObject("user", userFormDto);
        } else {
            UserFormDto userFormDtoInModel = (UserFormDto) model.getAttribute("user");
            if (userFormDto.getId() != null && !Objects.equals(userFormDtoInModel.getId(), userFormDto.getId())) {
                modelAndView.addObject("user", userFormDto);
            }
        }

        if (Objects.nonNull(userId)) {
            modelAndView.setViewName("users/edit");
        } else {
            modelAndView.setViewName("users/register");
        }

        final List<String> allRolesName = Arrays.stream(RoleEnum.values()).map(RoleEnum::name).collect(Collectors.toList());

        model.addAttribute("allRolesName", allRolesName);
        model.addAttribute("resourceImageId", userFormDto.getImage());
        modelAndView.addObject("profileMode", profileMode);

        return modelAndView;
    }

    /**
     * Maneja el registro o la edición de un usuario a través del formulario.
     *
     * @param userId ID del usuario (opcional).
     * @param user DTO del usuario con los datos enviados.
     * @param bindingResult Resultado de la validación del formulario.
     * @param imageFile Archivo de imagen enviado (opcional).
     * @param redirectAttributes Atributos para redirección.
     * @param model Modelo para pasar datos a la vista.
     * @return Redirección a la vista de perfil del usuario o al formulario en caso de error.
     */
    @PostMapping({"/save", "/edit/{userId}"})
    public RedirectView userCreateOrEditHandler(
            @PathVariable(name = "userId", required = false) String userId,
            @Valid @ModelAttribute("user") UserFormDto user, BindingResult bindingResult,
            @RequestParam(value = "file", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            final ModelAndView modelAndView = new ModelAndView();
            modelAndView.addAllObjects(model.asMap());
            if (!model.containsAttribute("user")) {
                modelAndView.addObject("user", user);
            }
            List<String> allRolesName = Arrays.stream(RoleEnum.values()).map(RoleEnum::name).collect(Collectors.toList());

            model.addAttribute("allRolesName", allRolesName);
            model.addAttribute("resourceImageId", user.getImage());

            final String maybeParam = Optional.ofNullable(userId)
                    .map("/%s"::formatted)
                    .orElse(StringUtils.EMPTY);

            modelAndView.getModel().forEach(redirectAttributes::addFlashAttribute);

            if (maybeParam.isEmpty()) {
                return new RedirectView("/web/users/register");
            } else {
                return new RedirectView("/web/users/edit%s".formatted(maybeParam));
            }
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            Optional<ResourceIdDto> resourceIdDtoOptional = storeFacade.saveResource(imageFile, null);
            resourceIdDtoOptional.ifPresent(resourceIdDto -> {
                UUIDHelper.mapStringToUUID(user.getImage()).ifPresent(storeFacade::deleteResource);
                user.setImage(resourceIdDto.resourceId().toString());
            });
        }

        UserDto userDto = userService.registerOrUpdatedUser(user);

        redirectAttributes.addFlashAttribute("message", "Usuario guardado correctamente!");
        return new RedirectView("/web/users/profile/%s".formatted(userDto.getId()));
    }

    /**
     * Maneja la visualización de la lista de usuarios con paginación y búsqueda.
     *
     * @param page Número de página solicitado (por defecto 0).
     * @param pageSize Tamaño de la página (por defecto 10).
     * @param searchUserRecord Objeto con los criterios de búsqueda.
     * @param model Modelo para pasar datos a la vista.
     * @return Nombre de la vista que muestra la lista de usuarios.
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public String listPageUsersHandler(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            @ModelAttribute("searchUserRecord") SearchUserRecord searchUserRecord,
            Model model) {

        final PageDTO<UserDto> pageUserDTO = userService.searchUsers(page, pageSize, searchUserRecord);
        model.addAttribute("pageUserDto", pageUserDTO);
        model.addAttribute("userAuth", userService.findUserAuthenticated().get());
        return "users/list";
    }
}