package com.tokioschool.filmapp.repositories;

import com.tokioschool.filmapp.domain.Movie;
import com.tokioschool.filmapp.projections.ResultMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieDao extends JpaRepository<Movie,Long> {

        /**
         * Search all movies that contains the world given in the title
         *
         * @param title
         * @return
         */
        List<ResultMovie> findMovieByTitleContainsIgnoreCase(String title);

        /**
         * Interval inclusive, search: releaseYear c [yearMin,yearMax]
         *
         * @param yearMin
         * @param yearMax
         * @return
         */
        List<ResultMovie> findMovieByReleaseYearBetween(Integer yearMin,Integer yearMax);

        List<ResultMovie> findMovieByReleaseYearIs(Integer year);
}
