package com.tokioschool.storeapp.controller;

import com.tokioschool.storeapp.core.exception.InternalErrorException;
import com.tokioschool.storeapp.core.exception.NotFoundException;
import com.tokioschool.storeapp.dto.store.ResourceContentDto;
import com.tokioschool.storeapp.dto.store.ResourceIdDto;
import com.tokioschool.storeapp.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

/**
 * Controlador para operaciones relacionadas con los recursos de la tienda.
 *
 * Este controlador proporciona endpoints para gestionar recursos, incluyendo
 * obtener, crear y eliminar recursos.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/store/api/resource")
@Tag(name = "store", description = "Operaciones relacionadas con los recursos de la tienda")
public class StoreApiController {

    private final StoreService storeService;

    /**
     * Endpoint para obtener un recurso por su ID.
     *
     * @param resourceId ID del recurso a obtener.
     * @return Una respuesta HTTP con el contenido del recurso y un código de estado 200 (OK).
     * @throws NotFoundException Si el recurso no se encuentra.
     */
    @Operation(
            summary = "Obtener recurso por ID",
            description = "Este endpoint permite obtener un recurso utilizando su ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Recurso obtenido exitosamente",
                            content = @Content(schema = @Schema(implementation = ResourceContentDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Operación no autorizada",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Recurso no encontrado",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            },
            security = @SecurityRequirement(name = "auth-openapi")
    )
    @GetMapping(value = "/{resourceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourceContentDto> getResourceHandler(
            @NotNull @PathVariable UUID resourceId) {

        ResourceContentDto resourceContentDto = storeService.findResource(resourceId)
                .orElseThrow(() -> new NotFoundException("Resource with id: %s not found!".formatted(resourceId)));

        return ResponseEntity.ok(resourceContentDto);
    }

    /**
     * Endpoint para crear un nuevo recurso.
     *
     * @param description Descripción opcional del recurso.
     * @param multipartFile Archivo del recurso a subir.
     * @return Una respuesta HTTP con el ID del recurso creado y un código de estado 201 (CREATED).
     * @throws InternalErrorException Si ocurre un error al guardar el recurso.
     */
    @Operation(
            summary = "Crear un nuevo recurso",
            description = "Este endpoint permite subir un nuevo recurso al sistema.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Recurso creado exitosamente",
                            content = @Content(schema = @Schema(implementation = ResourceIdDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Operación no autorizada",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            },
            security = @SecurityRequirement(name = "auth-openapi")
    )
    @PostMapping(value = {"", "/upload"}, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourceIdDto> createResourceHandler(
            @RequestPart(value = "description", required = false) String description,
            @RequestPart("content") MultipartFile multipartFile) {

        ResourceIdDto resourceIdDto = storeService.saveResource(multipartFile, description)
                .orElseThrow(() -> new InternalErrorException("There's been an error, try it again later"));

        return ResponseEntity.status(HttpStatus.CREATED).body(resourceIdDto);
    }

    /**
     * Endpoint para eliminar un recurso por su ID.
     *
     * @param resourceId ID del recurso a eliminar.
     * @return Una respuesta HTTP con un código de estado 204 (NO CONTENT) si la eliminación es exitosa.
     */
    @Operation(
            summary = "Eliminar un recurso por ID",
            description = "Este endpoint permite eliminar un recurso utilizando su ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Recurso eliminado exitosamente",
                            content = @Content(schema = @Schema(implementation = Void.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Operación no autorizada",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            },
            security = @SecurityRequirement(name = "auth-openapi")
    )
    @DeleteMapping(value = "/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteResourceHandler(@Valid @NotNull @PathVariable UUID resourceId) {
        storeService.deleteResource(resourceId);
        return ResponseEntity.noContent().build();
    }
}