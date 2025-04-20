package com.tokioschool.filmweb.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web")
public class IndexMvcController {

    @GetMapping({"","/"})
    public String home() {
        return "redirect:/web/index";  // Redirige a la vista index
    }

    @GetMapping("/index")
    public String getIndexPageHandler() {
        return "index";
    }

}
