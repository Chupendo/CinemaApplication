package com.tokioschool.filmweb.controllers;

import com.tokioschool.core.exception.OperationNotAllowException;
import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.user.UserDto;
import com.tokioschool.filmapp.dto.user.UserFormDto;
import com.tokioschool.filmapp.enums.RoleEnum;
import com.tokioschool.filmapp.records.SearchUserRecord;
import com.tokioschool.filmapp.services.user.UserService;
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

@Controller
@RequestMapping("/web/users")
@RequiredArgsConstructor
@SessionAttributes({"user"})
public class UserMvcController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    private final String UPLOAD_DIR = "src/main/resources/static/images/";

    @GetMapping({"/profile","/profile/{userId}"})
    @PreAuthorize("isAuthenticated()")
    public String profileHandler(@PathVariable(name="userId",required = false) String userId,
                                 Model model) {
        UserFormDto userFormDto;
        if(userId != null){
            userFormDto = Optional.of(userId)
                    .map(userService::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(userDto -> modelMapper.map(userDto, UserFormDto.class))
                    .orElseThrow( () -> new UsernameNotFoundException("Usuario no encontrado.") );
        }else{
            userFormDto = userService.findUserAuthenticated()
                    .map(userDto -> modelMapper.map(userDto, UserFormDto.class))
                    .orElseThrow( () -> new AuthenticationCredentialsNotFoundException("Usuario no autenticado") );
        }

        // reload user object
        model.addAttribute("user",userFormDto);

        // Devuelve la misma vista pero reenviando internamente (sin perder el binding result or info context of request)
        return ("forward:/web/users/edit/%s?mode=%s".formatted(userFormDto.getId(),true) );
    }

    @GetMapping({"/register","/edit/{userId}"})
    public ModelAndView userCreateOrEditHandler(@PathVariable(name="userId",required = false) String userId,
                                                @RequestParam(value="mode",defaultValue = "false",required = false) boolean profileMode,
                                                Model model) {
        final ModelAndView modelAndView =  new ModelAndView();
        if( Objects.nonNull(userId) && !userService.operationEditAllow(userId) ) {
            throw new OperationNotAllowException("Operation not allow");
        }

        final UserFormDto userFormDto = Optional.ofNullable(userId)
                .map(userService::findById)
                .map(dto -> modelMapper.map(dto, UserFormDto.class))
                .orElse(UserFormDto.builder().build());

        modelAndView.addAllObjects(model.asMap());

        if( !model.containsAttribute("user")){
            modelAndView.addObject("user", userFormDto );
        }else{
            UserFormDto userFormDtoInModel = (UserFormDto) model.getAttribute("user");
            if( userFormDto.getId() != null && !Objects.equals( userFormDtoInModel.getId(),userFormDto.getId())){
                modelAndView.addObject("user", userFormDto );
            }
        }

        if( Objects.nonNull(userId) ){
            modelAndView.setViewName("users/edit");
        }else{
            modelAndView.setViewName("users/register");
        }

        final List<String> allRolesName = Arrays.stream(RoleEnum.values()).map(RoleEnum::name).collect(Collectors.toList());

        model.addAttribute("allRolesName", allRolesName);
        model.addAttribute("resourceImageId",null);
        modelAndView.addObject("profileMode", profileMode);

        return modelAndView;
    }

    @PostMapping({"/save","/edit/{userId}"})
    public RedirectView userCreateOrEditHandler(
            @PathVariable(name="userId",required = false) String userId,
            @Valid  @ModelAttribute("user") UserFormDto user, BindingResult bindingResult,
            @RequestParam(value = "file",required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Model model) {

        if(bindingResult.hasErrors()){
            // se crea un nuevo get a partir de los datos del formulario
            final ModelAndView modelAndView =  new ModelAndView();

            modelAndView.addAllObjects(model.asMap());
            if(!model.containsAttribute("user") ){
                modelAndView.addObject("user", user);
            }
            List<String> allRolesName = Arrays.stream(RoleEnum.values()).map(RoleEnum::name).collect(Collectors.toList());

            model.addAttribute("allRolesName", allRolesName);
            model.addAttribute("resourceImageId",null);

            // se converte el model and view en elementos y lo adaptos a elemtentos redireccionables
            final String maybeParam = Optional.ofNullable(userId)
                    .map("/%s"::formatted)
                    .orElse(StringUtils.EMPTY);

            // se añade el modelo al redirect
            modelAndView.getModel().forEach(redirectAttributes::addFlashAttribute);

            // se envia una peticion nueva
            if( maybeParam.isEmpty() ){
                return new RedirectView("/web/users/register");
            }else{
                return new RedirectView("/web/users/edit%s".formatted(maybeParam));
            }
        }

        if (imageFile!=null && !imageFile.isEmpty()) {
            // TODO gestion de resoruse con facde

        }

        // Guardar el usuario
        UserDto userDto = userService.registerUser(user); // TODO Descomentar

        // Mensaje de éxito
        redirectAttributes.addFlashAttribute("message", "Usuario guardado correctamente!");
        return new RedirectView("/web/users/profile/%s".formatted( userDto.getId() )); // Redirigir a la lista de usuarios o página de éxito
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public String listPageUsersHandler(
            @RequestParam(value = "page",required = false, defaultValue = "0") int page,
            @RequestParam(value = "pageSize", required = false,defaultValue = "10") int pageSize,
            @ModelAttribute("searchUserRecord") SearchUserRecord searchUserRecord,
            Model model) {

        //     PageDTO<UserDto> searchUsers(int page, int pageSize, SearchUserRecord searchUserRecord);
        //final Object attr = model.getAttribute("searchUserRecord") ;
        //final SearchUserRecord searchUserRecord =  ( attr instanceof SearchUserRecord data )  ? data : null;

        final PageDTO<UserDto> pageUserDTO = userService.searchUsers(page,pageSize, searchUserRecord);
        model.addAttribute( "pageUserDto", pageUserDTO);
        model.addAttribute( "userAuth", userService.findUserAuthenticated().get() );
        return "users/list";
    }
}
