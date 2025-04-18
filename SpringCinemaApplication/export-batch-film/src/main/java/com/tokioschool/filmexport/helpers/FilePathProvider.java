package com.tokioschool.filmexport.helpers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Proveedor de rutas de archivo para la exportación de datos de películas.
 *
 * Esta clase proporciona métodos para generar rutas de archivo, encabezados y
 * líneas de contenido para archivos CSV utilizados en la exportación de datos
 * de películas. Utiliza `MessageSource` para la internacionalización de los
 * mensajes y formatos.
 *
 * Anotaciones utilizadas:
 * - `@Component`: Marca esta clase como un componente Spring para la inyección de dependencias.
 * - `@RequiredArgsConstructor`: Genera un constructor con los argumentos requeridos para las dependencias finales.
 *
 * @author andres.rpenuela
 * @version 1.0.
 */
@Component
@RequiredArgsConstructor
public class FilePathProvider {

    // Constantes
    /** Clave del mensaje que define la ruta de salida del archivo. */
    private static final String FILE_EXPORT_BATCH = "file.output.path";

    /** Encabezado predeterminado para los archivos CSV exportados. */
    public static final String FILE_EXPORT_HEADER = "film.export.csv.header";

    // Inyección de dependencias
    /** Fuente de mensajes para la internacionalización. */
    private final MessageSource messageSource;

    /**
     * Obtiene un recurso de archivo de salida basado en el ID del trabajo.
     *
     * @param jobId ID del trabajo para el que se genera el archivo.
     * @return Recurso del sistema de archivos que representa el archivo de salida.
     */
    public Resource getOutputFile(Long jobId) {
        return new FileSystemResource( getOutputFileName(jobId) );
    }

    /**
     * Genera el nombre del archivo de salida basado en el ID del trabajo y la fecha actual.
     *
     * @param jobId ID del trabajo para el que se genera el archivo.
     * @return Nombre del archivo de salida como cadena de texto.
     */
    public String getOutputFileName(Long jobId) {
        final String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        return messageSource.getMessage(FILE_EXPORT_BATCH, new Object[]{date, jobId}, Locale.getDefault());
    }

    /**
     * Construye el encabezado del archivo CSV para la exportación de películas.
     *
     * @return Encabezado del archivo CSV como cadena de texto.
     */
    public String buildHeader() {
        return messageSource.getMessage(
                FILE_EXPORT_HEADER,
                null,
                Locale.getDefault()
        );
    }

    /**
     * Compone una línea de contenido para el archivo CSV con los datos de una película.
     *
     * @param filmId ID de la película.
     * @param title Título de la película.
     * @param releaseYear Año de lanzamiento de la película.
     * @return Línea de contenido del archivo CSV como cadena de texto.
     */
    public String composeFilmCsvLine(Long filmId, String title, int releaseYear) {
        return messageSource.getMessage(
                "film.export.csv",
                new Object[]{filmId, title, releaseYear},
                Locale.getDefault()
        );
    }

    /**
     * Construye un mensaje de éxito para la exportación de una película.
     *
     * @param filmId ID de la película exportada.
     * @param title Título de la película exportada.
     * @param releaseYear Año de lanzamiento de la película exportada.
     * @return Mensaje de éxito como cadena de texto.
     */
    public String buildExportSuccessMessage(Long filmId, String title, int releaseYear) {
        return messageSource.getMessage(
                "film.export.success",
                new Object[]{filmId, title, releaseYear},
                Locale.getDefault()
        );
    }

    /**
     * Construye un mensaje de error para la exportación de una película.
     *
     * @param filmId ID de la película que no pudo ser exportada.
     * @param title Título de la película que no pudo ser exportada.
     * @return Mensaje de error como cadena de texto.
     */
    public String buildExportErrorMessage(Long filmId, String title) {
        return messageSource.getMessage(
                "film.export.error",
                new Object[]{filmId, title},
                Locale.getDefault()
        );
    }
}