package com.tokioschool.filmapp.dto.movie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL) // Ignora valores nulos ao serializar
@Builder
@NoArgsConstructor @AllArgsConstructor
public class FilterMovie{

        @JsonProperty("title")
        private String title;

        @JsonProperty("yearMin")
        private Integer yearMin;

        @JsonProperty("yearMax")
        private Integer yearMax;
}
