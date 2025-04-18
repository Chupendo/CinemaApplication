package com.tokioschool.filmapp.services.exportsFilms;

import com.tokioschool.filmapp.records.ExportFilmRecord;

import java.util.List;

public interface ExportFilmService {
    List<ExportFilmRecord> exportFilm(Long jobId);
}
