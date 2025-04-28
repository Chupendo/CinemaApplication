package com.tokioschool.filmweb.controllers.mvc;

import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.movie.MovieDto;
import com.tokioschool.filmapp.records.RangeReleaseYear;
import com.tokioschool.filmapp.records.SearchMovieRecord;
import com.tokioschool.filmapp.services.movie.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web/films")
@RequiredArgsConstructor
public class FilmMvcController {

    private final MovieService movieService;


    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String getFilmsHandler(Model model){

        final SearchMovieRecord searchMovieRecord = SearchMovieRecord
                .builder()
                .rangeReleaseYear(RangeReleaseYear.builder().build())
                .build();

        if( !model.containsAttribute("searchMovieRecord")){
            model.addAttribute( "searchMovieRecord", searchMovieRecord);
        }

        PageDTO<MovieDto> pageMovieDto =  movieService.searchMovie(searchMovieRecord);
        model.addAttribute( "pageMovieDto", pageMovieDto);
        return "movies/list";
    }

}
