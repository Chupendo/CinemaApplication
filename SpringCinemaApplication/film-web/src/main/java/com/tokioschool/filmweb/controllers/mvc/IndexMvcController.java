package com.tokioschool.filmweb.controllers.mvc;

import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.movie.MovieDto;
import com.tokioschool.filmapp.services.movie.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web")
@RequiredArgsConstructor
public class IndexMvcController {

    private final MovieService movieService;

    @GetMapping({"","/"})
    public String home() {
        return "redirect:/web/index";  // Redirige a la vista index
    }

    @GetMapping("/index")
    public String getIndexPageHandler(Model model) {
        PageDTO<MovieDto> movieDtoPageDTO = movieService.searchMovie();
        model.addAttribute("pageMovieDto", movieDtoPageDTO);
        return "index";
    }

}
