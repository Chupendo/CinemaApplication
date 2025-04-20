package com.tokioschool.filmweb.controllers;

import com.tokioschool.filmapp.dto.user.RoleDto;
import com.tokioschool.filmapp.dto.user.UserDto;
import com.tokioschool.filmapp.services.role.RoleService;
import com.tokioschool.filmapp.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web/users")
@SessionAttributes("user")
@RequiredArgsConstructor
public class UserMvcController {

    private final UserService userService;
    private final RoleService roleService;

    @ModelAttribute("user")
    public UserDto getUserDto() {
        List<RoleDto> roleDtos = roleService.getAllRoles();
        return UserDto.builder().name("Andres").roles(roleDtos).build();
    }

    @GetMapping("/register")
    public String getRegisterPageHandler(Model model) {

        model.addAttribute("allRoles", roleService.getAllRoles());

        List<Long> userRoleIds = Optional.ofNullable(getUserDto().getRoles())
                .orElse(Collections.emptyList())
                .stream()
                .map(RoleDto::getId)
                .collect(Collectors.toList());

        model.addAttribute("userRoleIds", userRoleIds);
        model.addAttribute("resourceImageId",null);

        return "users/register";
    }
}
