package com.tokioschool.filmapp.records;

import lombok.Builder;

@Builder
public record ExportFilmRecord(Long filmId, String title, int releaseYear){
}
