package com.tokioschool.filmapp.repositories;

import com.tokioschool.filmapp.domain.Artist;
import com.tokioschool.filmapp.enums.TYPE_ARTIST;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistDao extends JpaRepository<Artist,Long> {

    List<Artist> getArtistsByTypeArtist(TYPE_ARTIST typeArtist);
}
