package com.tokioschool.filmapp.records;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@JsonIgnoreProperties
@Jacksonized
public record SearchUserRecord(String name,String surname,String username,String email) {
}
