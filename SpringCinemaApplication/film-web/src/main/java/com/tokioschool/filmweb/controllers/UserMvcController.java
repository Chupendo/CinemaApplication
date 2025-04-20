package com.tokioschool.filmweb.controllers;

import com.tokioschool.filmapp.dto.user.UserFormDto;
import com.tokioschool.filmapp.enums.RoleEnum;
import com.tokioschool.filmapp.services.role.RoleService;
import com.tokioschool.filmapp.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web/users")
@SessionAttributes("user")
@RequiredArgsConstructor
public class UserMvcController {

    private final UserService userService;
    private final RoleService roleService;
    private final ModelMapper modelMapper;

    private final String UPLOAD_DIR = "src/main/resources/static/images/";

    @ModelAttribute("user")
    public UserFormDto getUserDto() {
        List<String> roleDtos = Arrays.stream(RoleEnum.values()).map(RoleEnum::name).collect(Collectors.toList());
        return UserFormDto.builder().name("Andres").birthDate(LocalDate.of(1992,07,06)).roles(roleDtos).build();
    }

    @GetMapping({"/register","/edit/{userId}"})
    public ModelAndView userCreateOrEditHandler(@PathVariable(name="userId",required = false) String userId,
                                         Model model) {
        final Optional<UserFormDto> userFormDtoOptional = Optional.ofNullable(userId)
                .map(userService::findById)
                .map(dto -> modelMapper.map(dto, UserFormDto.class));

        final ModelAndView modelAndView =  new ModelAndView();

        modelAndView.addAllObjects(model.asMap());
        if(userFormDtoOptional.isPresent()){
            modelAndView.setViewName("users/edit");
            // si existe en el modelo
            modelAndView.addObject("user", userFormDtoOptional.get());
        }else{
            modelAndView.setViewName("users/register");
        }

        List<String> allRolesName = Arrays.stream(RoleEnum.values()).map(RoleEnum::name).collect(Collectors.toList());

        model.addAttribute("allRolesName", allRolesName);
        model.addAttribute("resourceImageId",null);

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
            if(!model.containsAttribute("user")){
                modelAndView.addObject("user", user);
            }
            List<String> allRolesName = Arrays.stream(RoleEnum.values()).map(RoleEnum::name).collect(Collectors.toList());

            model.addAttribute("allRolesName", allRolesName);
            model.addAttribute("resourceImageId",null);

            // se converte el model and view en elementos y lo adaptos a elemtentos redireccionables
            final String maybeParam = Optional.ofNullable(userId)
                    .map("/%d"::formatted)
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
            //throw new IOException("No se ha subido la imagen"); //  TODO borrar
            // Guardar el usuario
            userService.registerUser(user);

            // Mensaje de éxito
            redirectAttributes.addFlashAttribute("message", "Usuario guardado correctamente!");
            return new RedirectView("/login"); // Redirigir a la lista de usuarios o página de éxito

    }

}
