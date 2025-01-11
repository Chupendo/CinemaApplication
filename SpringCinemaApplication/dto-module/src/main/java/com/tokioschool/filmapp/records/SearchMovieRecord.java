package com.tokioschool.filmapp.records;

import lombok.Builder;

@Builder
public record SearchMovieRecord(String title,RangeReleaseYear rangeReleaseYear,int page, int pageSize){
}
