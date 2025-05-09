package com.tokioschool.filmapp.records;

import lombok.Builder;
import org.springframework.lang.NonNull;


@Builder
public record SearchArtistRecord(String name, String surname, String type) {
}