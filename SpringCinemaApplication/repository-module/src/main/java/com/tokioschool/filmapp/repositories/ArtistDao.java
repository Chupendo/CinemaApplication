package com.tokioschool.filmapp.repositories;

import com.tokioschool.filmapp.domain.Artist;
import com.tokioschool.filmapp.enums.TYPE_ARTIST;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad {@link Artist}.
 *
 * Esta interfaz extiende {@link JpaRepository} para proporcionar métodos CRUD
 * y consultas personalizadas para la entidad Artist.
 *
 * Anotaciones:
 * - {@link Repository}: Marca esta interfaz como un componente de acceso a datos de Spring.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Repository
public interface ArtistDao extends JpaRepository<Artist, Long>, JpaSpecificationExecutor<Artist> {

    /**
     * Obtiene una lista de artistas filtrados por su tipo.
     *
     * @param typeArtist El tipo de artista a filtrar, basado en {@link TYPE_ARTIST}.
     * @return Una lista de artistas que coinciden con el tipo especificado.
     */
    List<Artist> getArtistsByTypeArtist(TYPE_ARTIST typeArtist);
}