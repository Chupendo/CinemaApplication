package com.tokioschool.filmapp.services.artist;

import com.tokioschool.core.exception.NotFoundException;
import com.tokioschool.filmapp.dto.artist.ArtistDto;

import java.util.List;

public interface ArtistService {

    List<ArtistDto> findByAll();

    ArtistDto registerArtist(ArtistDto artistDto) throws IllegalArgumentException;

    ArtistDto findById(Long artistId) throws NotFoundException;
}
