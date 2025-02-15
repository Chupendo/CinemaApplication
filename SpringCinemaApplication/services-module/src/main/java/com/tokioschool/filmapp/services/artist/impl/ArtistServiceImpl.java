package com.tokioschool.filmapp.services.artist.impl;

import com.tokioschool.core.exception.NotFoundException;
import com.tokioschool.filmapp.domain.Artist;
import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.enums.TYPE_ARTIST;
import com.tokioschool.filmapp.repositories.ArtistDao;
import com.tokioschool.filmapp.services.artist.ArtistService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final ArtistDao artistDao;
    private final ModelMapper modelMapper;

    @Override
    public List<ArtistDto> findByAll() {
        return artistDao.findAll().stream().map(artist -> modelMapper.map(artist,ArtistDto.class)).toList();
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ArtistDto registerArtist(ArtistDto artistDto) throws IllegalArgumentException{
        Artist artist = Artist.builder().build();
        
        return populationCreateOrEditArtist(artist,artistDto);
    }

    @Override
    public ArtistDto findById(Long artistId) throws NotFoundException {
        return Optional.ofNullable(artistId)
                .map(artistDao::findById)
                .map(artist -> modelMapper.map(artist,ArtistDto.class))
                .orElseThrow(()->new NotFoundException("Artist with id: %d not found".formatted(artistId)));
    }

    protected ArtistDto populationCreateOrEditArtist(Artist artist, ArtistDto artistDto) throws IllegalArgumentException {

        artist.setName(artistDto.getName());
        artist.setSurname(artistDto.getSurname());
        artist.setTypeArtist(TYPE_ARTIST.valueOf(artistDto.getTypeArtist().toUpperCase()));

        artist = artistDao.save(artist);

        return  modelMapper.map(artist,ArtistDto.class);
    }
}
