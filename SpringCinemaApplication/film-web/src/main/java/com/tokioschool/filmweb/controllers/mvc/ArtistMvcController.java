package com.tokioschool.filmweb.controllers.mvc;

import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.enums.TYPE_ARTIS_DTO;
import com.tokioschool.filmapp.records.SearchArtistRecord;
import com.tokioschool.filmapp.services.artist.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/web/artists")
@RequiredArgsConstructor
public class ArtistMvcController {

    private final ArtistService artistService;

    @GetMapping("/list")
    public String listPageArtis(
            @RequestParam(value = "page",required = false, defaultValue = "0") int page,
            @RequestParam(value = "pageSize", required = false,defaultValue = "10") int pageSize,
            @ModelAttribute("searchArtistRecord") SearchArtistRecord searchArtistRecord,
            Model model) {

            final PageDTO<ArtistDto> pageArtistDto = artistService.searchArtist(page, pageSize,searchArtistRecord);
            model.addAttribute( "pageArtistDto", pageArtistDto);
            return "artists/list";
    }
}
