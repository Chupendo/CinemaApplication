package com.tokioschool.filmapp.dto.movie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * Clase DTO (Data Transfer Object) para representar los filtros de búsqueda de películas.
 *
 * Esta clase se utiliza para transferir los datos relacionados con los filtros
 * aplicados en la búsqueda de películas, como el título y el rango de años.
 * Es inmutable y utiliza la anotación `@Jacksonized` de Lombok para la serialización/deserialización.
 *
 * Los valores nulos se excluyen automáticamente durante la serialización JSON.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Data
@Jacksonized
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL) // Ignora valores nulos al serializar
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterMovie {

        /**
         * Título de la película a buscar.
         */
        @JsonProperty("title")
        private String title;

        /**
         * Año mínimo para filtrar las películas.
         */
        @JsonProperty("yearMin")
        private Integer yearMin;

        /**
         * Año máximo para filtrar las películas.
         */
        @JsonProperty("yearMax")
        private Integer yearMax;
}