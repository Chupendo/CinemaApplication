package com.tokioschool.filmweb.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginMvcController {

    @GetMapping({"","/"})
    public String homeHandler() {
        return "redirect:/web?lang=es";  // Redirige a la vista index
    }

    @GetMapping("/login")
    public String loginHandler(){
        return "login";
    }

    @GetMapping("/logout")
    public String logoutHandler(){
        return "logout";
    }
}
