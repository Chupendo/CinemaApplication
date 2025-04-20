package com.tokioschool.filmweb.controllers;

import com.tokioschool.filmapp.dto.user.UserFormDto;
import com.tokioschool.filmapp.enums.RoleEnum;
import com.tokioschool.filmapp.services.role.RoleService;
import com.tokioschool.filmapp.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web/users")
@SessionAttributes("user")
@RequiredArgsConstructor
public class UserMvcController {

    private final UserService userService;
    private final RoleService roleService;

    private final String UPLOAD_DIR = "src/main/resources/static/images/";

    @ModelAttribute("user")
    public UserFormDto getUserDto() {
        List<String> roleDtos = Arrays.stream(RoleEnum.values()).map(RoleEnum::name).collect(Collectors.toList());
        return UserFormDto.builder().name("Andres").birthDate(LocalDate.of(1992,07,06)).roles(roleDtos).build();
    }

    @GetMapping("/register")
    public String getRegisterPageHandler(Model model) {

        List<String> allRolesName = Arrays.stream(RoleEnum.values()).map(RoleEnum::name).collect(Collectors.toList());

        model.addAttribute("allRolesName", allRolesName);
        model.addAttribute("resourceImageId",null);

        return "users/register";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute UserFormDto user,
                           @RequestParam(value = "file",required = false) MultipartFile imageFile,
                           RedirectAttributes redirectAttributes) {

        try {
            if (imageFile!=null && !imageFile.isEmpty()) {
                // TODO gestion de resoruse con facde

            }
            throw new IOException("No se ha subido la imagen"); //  TODO borrar
            // Guardar el usuario
//            userService.registerUser(user);

            // Mensaje de éxito
//            redirectAttributes.addFlashAttribute("message", "Usuario guardado correctamente!");
//            return "redirect:/users";  // Redirigir a la lista de usuarios o página de éxito

        } catch (IOException e) {
            // Si ocurre un error al guardar la imagen
            redirectAttributes.addFlashAttribute("error", "Hubo un error al guardar la imagen.");
            return "redirect:/users";  // Redirigir a la lista de usuarios o página de error
        }
    }

}
