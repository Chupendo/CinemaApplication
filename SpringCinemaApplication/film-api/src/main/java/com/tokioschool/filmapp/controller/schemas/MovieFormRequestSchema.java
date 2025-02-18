package com.tokioschool.filmapp.controller.schemas;

import com.tokioschool.filmapp.dto.movie.MovieDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

/**
 * Se le dice a Swagger que use el esquema de la clase MovieFormRequest para interpretar el multipart/form-data.
 * @Schema(implementation = MovieFormRequestSchema.class):
 *
 *
 * Clase MovieFormRequestSchema:
 *
 * Define
 *  - image como binario.
 *  - description como cadenas.
 *  - movieFormDto  como objeto
 *
 *
 * Swagger ahora mostrará un campo para cargar un archivo.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Data
@Schema(description = "Formulario para el registro de una película",contentMediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
public class MovieFormRequestSchema {

    @Schema(description = "Imagen de la película", type = "string", format = "binary")
    private MultipartFile image;

    @Schema(description = "Descripción de la película", example = "Una película de ciencia ficción")
    private String description;

    @Schema(description = "Datos de la película en formato JSON",type = "object", format = "movieDto", example = "{\"title\": \"Inception\", \"director\": \"Christopher Nolan\", \"releaseYear\": 2010, \"genre\": \"Sci-Fi\"}")
    private MovieDto movieFormDto;
}