package com.tokioschool.filmweb.controllers.mvc;

import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.enums.TYPE_ARTIS_DTO;
import com.tokioschool.filmapp.records.SearchArtistRecord;
import com.tokioschool.filmapp.repositories.ArtistDao;
import com.tokioschool.filmapp.services.artist.ArtistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@Controller
@RequestMapping("/web/artists")
@RequiredArgsConstructor
public class ArtistMvcController {

    private final ArtistService artistService;
    private final ModelMapper modelMapper;

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String listPageArtis(
            @RequestParam(value = "page",required = false, defaultValue = "0") int page,
            @RequestParam(value = "pageSize", required = false,defaultValue = "10") int pageSize,
            @ModelAttribute("searchArtistRecord") SearchArtistRecord searchArtistRecord,
            Model model) {

            final PageDTO<ArtistDto> pageArtistDto = artistService.searchArtist(page, pageSize,searchArtistRecord);
            model.addAttribute( "pageArtistDto", pageArtistDto);
            return "artists/list";
    }

    @GetMapping( {"/profile/{artistId}"})
    @PreAuthorize("isAuthenticated()")
    public String profileHandler(@PathVariable(value="artistId",required = false) Long artisId,
                                            Model model){

        final ArtistDto artistDto = Optional.ofNullable(artisId)
                .map(artistService::findById)
                .orElseGet(() -> ArtistDto.builder().build());

        model.addAttribute("artist",artistDto);
        model.addAttribute("profileMode", Boolean.TRUE );
        model.addAttribute("isRegister", Boolean.FALSE );

        return "artists/form";
    }

    @GetMapping( {"/form","/form/","/form/{artistId}"})
    @PreAuthorize("isAuthenticated()")
    public String artistCreateOrEditHandler(@PathVariable(value="artistId",required = false) Long artisId,
                                            Model model){
        final boolean isRegister = (artisId == null);
        final ArtistDto artistDto = Optional.ofNullable(artisId)
                .map(artistService::findById)
                .orElseGet(() -> ArtistDto.builder().build());

        if(!model.containsAttribute("artist")){
            model.addAttribute("artist",artistDto);
        }

        model.addAttribute("isRegister", isRegister );
        model.addAttribute("profileMode", Boolean.FALSE );

        return "artists/form";
    }

    @PostMapping({"/form","/form/","/form/{artistId}"})
    @PreAuthorize("isAuthenticated()")
    public RedirectView formRegisterOrEditArtist(
            @PathVariable(value="artistId",required = false) Long artistId,
            @Valid @ModelAttribute(value="artist") ArtistDto artistDto, BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model){

        if(bindingResult.hasErrors()){
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("artist/form");
            modelAndView.addAllObjects( model.asMap() );

            if( !model.containsAttribute("artist")){
                modelAndView.addObject("artist",artistDto);
            }
            // se añade el modelo al redirect
            modelAndView.getModel().forEach(redirectAttributes::addFlashAttribute);

            // se converte el model and view en elementos y lo adaptos a elemtentos redireccionables
            final String maybeParam = Optional.ofNullable(artistId)
                    .map("/%s"::formatted)
                    .orElse(StringUtils.EMPTY);

            // se envia una peticion nueva
            if( maybeParam.isEmpty() ){
                return new RedirectView("/web/artists/form");
            }else{
                return new RedirectView("/web/artists/form%s".formatted(maybeParam));
            }
        }

        if(artistId == null) {
            artistService.registerArtist(artistDto);
        }else{
            artistService.updatedArtist(artistId,artistDto);
        }

        return new RedirectView("/web/artists/list");
    }
}
