package com.tokioschool.filmweb.controllers.rest;

import com.tokioschool.core.exception.NotFoundException;
import com.tokioschool.store.dto.ResourceContentDto;
import com.tokioschool.store.facade.StoreFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controlador REST para gestionar recursos.
 *
 * Este controlador maneja las solicitudes relacionadas con la obtención y descarga
 * de contenido de recursos almacenados en el sistema.
 *
 * Anotaciones utilizadas:
 * - `@RestController`: Marca esta clase como un controlador REST, combinando `@Controller` y `@ResponseBody`.
 * - `@RequestMapping`: Define la ruta base para las solicitudes relacionadas con recursos.
 * - `@RequiredArgsConstructor`: Genera un constructor con los argumentos requeridos.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceRestController {

    /** Fachada para gestionar la lógica de negocio relacionada con recursos. */
    private final StoreFacade storeFacade;

    /**
     * Maneja la obtención del contenido de un recurso.
     *
     * @param resourceId ID del recurso solicitado.
     * @return Una respuesta HTTP con el contenido del recurso en el cuerpo.
     * @throws NotFoundException Si el recurso no se encuentra.
     */
    @GetMapping({"", "/"})
    public ResponseEntity<byte[]> getResourceContentHandler(@RequestParam(value = "rsc") UUID resourceId) {
        final ResourceContentDto resourceContentDto = storeFacade.findResource(resourceId)
                .orElseThrow(() -> new NotFoundException("Resource not found! "));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resourceContentDto.contentType()))
                .contentLength(resourceContentDto.size())
                .body(resourceContentDto.content());
    }

    /**
     * Maneja la descarga del contenido de un recurso.
     *
     * Este metodo fuerza la descarga del recurso, configurando las cabeceras HTTP
     * necesarias para indicar el nombre del archivo y su tipo de contenido.
     *
     * @param resourceId ID del recurso solicitado.
     * @return Una respuesta HTTP con el contenido del recurso y cabeceras para descarga.
     * @throws NotFoundException Si el recurso no se encuentra.
     */
    @GetMapping("/downloads")
    public ResponseEntity<byte[]> downloadResourceContentHandler(@RequestParam(value = "rsc") UUID resourceId) {
        final ResourceContentDto resresourceContentDtoourceDto = storeFacade.findResource(resourceId)
                .orElseThrow(() -> new NotFoundException("Resource not found! "));

        // Configuración de cabeceras HTTP para forzar la descarga
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename= " + resresourceContentDtoourceDto.resourceName());
        httpHeaders.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(resresourceContentDtoourceDto.size()));
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, resresourceContentDtoourceDto.contentType());

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(resresourceContentDtoourceDto.content());
    }
}