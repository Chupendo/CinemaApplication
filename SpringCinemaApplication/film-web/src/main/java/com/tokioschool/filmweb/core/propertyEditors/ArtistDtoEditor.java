package com.tokioschool.filmweb.core.propertyEditors;

import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.services.artist.ArtistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;
import java.util.Optional;

/**
 * Editor personalizado para convertir cadenas de texto en objetos de tipo `ArtistDto`.
 *
 * Este editor se utiliza para manejar la conversión de datos enviados desde formularios
 * o vistas en la aplicación web, transformando un identificador de artista (en formato de texto)
 * en una instancia de `ArtistDto`.
 *
 * Anotaciones utilizadas:
 * - `@Component`: Marca esta clase como un componente de Spring para su inyección de dependencias.
 * - `@RequiredArgsConstructor`: Genera un constructor con los argumentos requeridos.
 * - `@Slf4j`: Habilita el registro de logs utilizando Lombok.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ArtistDtoEditor extends PropertyEditorSupport {

    /** Servicio para gestionar la lógica de negocio relacionada con artistas. */
    private final ArtistService artistService;

    /**
     * Convierte una cadena de texto en un objeto `ArtistDto`.
     *
     * Este metodo intenta convertir el texto proporcionado en un identificador numérico
     * y luego busca el artista correspondiente utilizando el servicio `ArtistService`.
     * Si el texto no es válido o no se encuentra el artista, se establece el valor como `null`.
     *
     * @param text La cadena de texto que representa el identificador del artista.
     * @throws IllegalArgumentException Si ocurre un error durante la conversión.
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        ArtistDto artistDto = null;
        try {
            artistDto = Optional.ofNullable(text)
                    .map(StringUtils::trimToNull) // Elimina espacios en blanco y valida que no sea nulo.
                    .filter(NumberUtils::isCreatable) // Verifica si el texto es un número válido.
                    .map(NumberUtils::createLong) // Convierte el texto en un número de tipo Long.
                    .map(artistService::findById) // Busca el artista por su ID.
                    .orElseGet(() -> null); // Devuelve null si no se encuentra el artista.
        } catch (NumberFormatException nfe) {
            log.error("Error al obtener el artista del editor {}", nfe.getMessage(), nfe);
        }
        setValue(artistDto); // Establece el valor convertido en el editor.
    }
}