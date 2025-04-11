package com.tokioschool.filmapp.controller.schemas;

import com.tokioschool.filmapp.dto.movie.MovieDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

/**
 * Clase que representa el esquema de un formulario para el registro de una película.
 *
 * Esta clase se utiliza para interpretar datos enviados en formato multipart/form-data,
 * permitiendo la carga de una imagen, una descripción y los datos de la película en formato JSON.
 *
 * Anotaciones de Swagger:
 * - {@link Schema}: Proporciona metadatos para la documentación de la API.
 *
 * Campos:
 * - `image`: Representa un archivo binario (imagen de la película).
 * - `description`: Representa una descripción en formato de texto.
 * - `movieFormDto`: Representa los datos de la película en formato JSON.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Data
@Schema(description = "Formulario para el registro de una película", contentMediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
public class MovieFormRequestSchema {

    /**
     * Imagen de la película.
     *
     * Este campo permite cargar un archivo binario que representa la imagen de la película.
     */
    @Schema(description = "Imagen de la película", type = "string", format = "binary")
    private MultipartFile image;

    /**
     * Descripción de la película.
     *
     * Este campo permite proporcionar una descripción en formato de texto.
     * Ejemplo: "Una película de ciencia ficción".
     */
    @Schema(description = "Descripción de la película", example = "Una película de ciencia ficción")
    private String description;

    /**
     * Datos de la película en formato JSON.
     *
     * Este campo permite enviar un objeto JSON que contiene los datos de la película.
     * Ejemplo: {"title": "Inception", "director": "Christopher Nolan", "releaseYear": 2010, "genre": "Sci-Fi"}.
     */
    @Schema(description = "Datos de la película en formato JSON", type = "object", format = "movieDto", example = "{\"title\": \"Inception\", \"director\": \"Christopher Nolan\", \"releaseYear\": 2010, \"genre\": \"Sci-Fi\"}")
    private MovieDto movieFormDto;
}