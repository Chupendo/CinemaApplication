package com.tokioschool.filmapp.controller;

import com.tokioschool.core.exception.NotFoundException;
import com.tokioschool.store.dto.ResourceContentDto;
import com.tokioschool.store.facade.StoreFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controlador REST para gestionar operaciones relacionadas con recursos.
 *
 * Este controlador proporciona endpoints para obtener y descargar contenido de recursos
 * almacenados en el sistema, integrándose con el servicio `store-api`.
 *
 * Anotaciones:
 * - {@link RestController}: Indica que esta clase es un controlador REST.
 * - {@link RequestMapping}: Define la ruta base para los endpoints de este controlador.
 * - {@link Tag}: Proporciona metadatos para la documentación de Swagger.
 *
 * Dependencias:
 * - {@link StoreFacade}: Facade para gestionar recursos almacenados.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/film/api/resources")
@Tag(name = "Resource", description = "Resources operations, integration with store-api")
@Slf4j
public class ResourceApiController {

    private final StoreFacade storeFacade;

    /**
     * Endpoint para obtener el contenido de un recurso.
     *
     * Este metodo busca un recurso por su ID y devuelve su contenido.
     *
     * @param resourceId ID del recurso a buscar.
     * @return Una respuesta HTTP con el contenido del recurso y un código de estado 200 (OK).
     * @throws NotFoundException Si el recurso no se encuentra o el servidor remoto está desconectado.
     */
    @Operation(
            summary = "Obtener contenido de un recurso",
            description = "Busca un recurso por su ID y devuelve su contenido.",
            parameters = {
                    @Parameter(
                            name = "resourceId",
                            description = "Identificación del recurso (por defecto: 4888138f-4a56-42a7-9bd6-da2751ded046)",
                            required = true,
                            example = "4888138f-4a56-42a7-9bd6-da2751ded046"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Contenido del recurso obtenido exitosamente",
                            content = @Content(
                                    mediaType = "*/*",
                                    schema = @Schema(implementation = byte[].class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Recurso no encontrado o servidor remoto desconectado",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @SecurityRequirement(name = "auth-openapi")
    @GetMapping(value = {"/", ""}, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> getContentResourceHandler(@RequestParam("resourceId") @Nonnull UUID resourceId) {
        final ResourceContentDto resourceContentDto = storeFacade.findResource(resourceId)
                .orElseThrow(() -> new NotFoundException("Resource with id: %s not found! or Store Server is disconnected.".formatted(resourceId)));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resourceContentDto.contentType()))
                .contentLength(resourceContentDto.size())
                .body(resourceContentDto.content());
    }

    /**
     * Endpoint para descargar el contenido de un recurso.
     *
     * Este metodo busca un recurso por su ID y permite descargar su contenido como un archivo adjunto.
     *
     * @param resourceId ID del recurso a descargar.
     * @return Una respuesta HTTP con el contenido del recurso como archivo adjunto y un código de estado 200 (OK).
     * @throws NotFoundException Si el recurso no se encuentra o el servidor remoto está desconectado.
     */
    @Operation(
            summary = "Descargar contenido de un recurso",
            description = "Busca un recurso por su ID y permite descargar su contenido como un archivo adjunto.",
            parameters = {
                    @Parameter(
                            name = "resourceId",
                            description = "Identificación del recurso (por defecto: 4888138f-4a56-42a7-9bd6-da2751ded046)",
                            required = true,
                            example = "4888138f-4a56-42a7-9bd6-da2751ded046"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Contenido del recurso descargado exitosamente",
                            content = @Content(
                                    mediaType = "*/*",
                                    schema = @Schema(implementation = byte[].class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Recurso no encontrado o servidor remoto desconectado",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @SecurityRequirement(name = "auth-openapi")
    @GetMapping(value = {"/download"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> downloadContentResourceHandler(@RequestParam("resourceId") @Nonnull UUID resourceId) {
        final ResourceContentDto resourceContentDto = storeFacade.findResource(resourceId)
                .orElseThrow(() -> new NotFoundException("Resource with id: %s not found! or Store Server is disconnected.".formatted(resourceId)));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resourceContentDto.resourceName());
        headers.add(HttpHeaders.CONTENT_LENGTH,String.valueOf(resourceContentDto.size()));
        headers.add(HttpHeaders.CONTENT_TYPE,resourceContentDto.contentType());

        return  ResponseEntity.ok()
                .headers(headers)
                .body(resourceContentDto.content());
    }
}
