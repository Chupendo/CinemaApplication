package com.tokioschool.filmapp.services.exportsFilms.impl;

import com.tokioschool.filmapp.domain.ExportFilm;
import com.tokioschool.filmapp.domain.Movie;
import com.tokioschool.filmapp.records.AverageRating;
import com.tokioschool.filmapp.records.ExportFilmRecord;
import com.tokioschool.filmapp.repositories.ExportFilmDao;
import com.tokioschool.filmapp.repositories.MovieDao;
import com.tokioschool.filmapp.services.exportsFilms.ExportFilmService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExportFilmServiceImpl implements ExportFilmService {

    private MovieDao movieDao;
    private ExportFilmDao exportFilmDao;

    @Override
    @Transactional
    public List<ExportFilmRecord> exportFilm(Long jobId) {
        // Todo crear un fichero csv con las pelicuas no exportadas por cada bath
        List<Movie> filmsNotExported = movieDao.findFilmsNotExported();
        List<ExportFilmRecord> exportFilmRecords = new ArrayList<>();

        filmsNotExported.forEach(filmNotExported ->{
            final ExportFilm exportFilm = ExportFilm.builder()
                    .movie( filmNotExported )
                    .jobId( jobId )
                    .build();
            exportFilmDao.save( exportFilm );

            final ExportFilmRecord exportFilmRecord = ExportFilmRecord.builder()
                    .filmId( filmNotExported.getId() )
                    .title( filmNotExported.getTitle() )
                    .releaseYear( filmNotExported.getReleaseYear() )
                    .build();
            exportFilmRecords.add( exportFilmRecord );
        });

        return exportFilmRecords;
    }
}
