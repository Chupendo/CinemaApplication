package com.tokioschool.storeapp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokioschool.storeapp.configuration.properties.StoreConfigurationProperties;
import com.tokioschool.storeapp.core.helper.FileHelper;
import com.tokioschool.storeapp.domain.ResourceDescription;
import com.tokioschool.storeapp.dto.store.ResourceContentDto;
import com.tokioschool.storeapp.dto.store.ResourceIdDto;
import com.tokioschool.storeapp.service.StoreService;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementación del servicio para la gestión de recursos en el sistema.
 *
 * Esta clase proporciona metodos para cargar, buscar y eliminar recursos,
 * utilizando un sistema de almacenamiento basado en archivos.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StoreServiceImpl implements StoreService {

    private final StoreConfigurationProperties storeConfigurationProperties;
    private final ObjectMapper objectMapper;

    /**
     * Inicializa el directorio de trabajo si no existe.
     *
     * Este metodo se ejecuta después de la construcción de la clase y asegura
     * que el directorio de trabajo definido en las propiedades de configuración
     * esté disponible.
     *
     * @throws IOException Si ocurre un error al crear el directorio.
     */
    @PostConstruct
    public void init() throws IOException {
        FileHelper.createWorkIfNotExists(Path.of(storeConfigurationProperties.relativePath()));
    }

    /**
     * Sube un recurso al sistema, guardando su contenido y metadatos.
     *
     * @param multipartFile El archivo que se desea cargar en el sistema.
     * @param description Información adicional sobre el recurso (opcional).
     * @return Un objeto `Optional` que contiene el identificador del recurso encapsulado en `ResourceIdDto`.
     */
    @Override
    public Optional<ResourceIdDto> saveResource(MultipartFile multipartFile, @Nullable String description) {
        // Verifica si el recurso está vacío
        if (multipartFile.isEmpty()) {
            log.error("The resource is empty");
            return Optional.empty();
        }

        // Construye los metadatos del recurso
        final ResourceDescription resourceDescription = ResourceDescription.builder()
                .resourceName(multipartFile.getOriginalFilename())
                .description(description)
                .size((int) multipartFile.getSize())
                .contentType(multipartFile.getContentType()).build();

        // Genera un identificador único para el recurso
        final ResourceIdDto resourceIdDto = ResourceIdDto.builder().resourceId(UUID.randomUUID()).build();
        final String resourceName = "%s".formatted(resourceIdDto.resourceId());
        final String resourceDescriptionName = "%s.json".formatted(resourceIdDto.resourceId());

        // Define las rutas para guardar el contenido y los metadatos
        final Path pathResourceToContent = storeConfigurationProperties.getResourcePathFromRelativePathGivenNameResource(resourceName);
        final Path pathResourceToDescription = storeConfigurationProperties.getResourcePathFromRelativePathGivenNameResource(resourceDescriptionName);

        // Guarda el contenido del recurso
        try {
            Files.write(pathResourceToContent, multipartFile.getBytes());
        } catch (IOException e) {
            log.error("Don't save content resource, cause: %s".formatted(e), e);
            return Optional.empty();
        }

        // Guarda los metadatos del recurso en formato JSON
        try {
            objectMapper.writeValue(pathResourceToDescription.toFile(), resourceDescription);
        } catch (IOException e) {
            log.error("Don't save meta data of resource, cause: %s".formatted(e));
            try {
                Files.deleteIfExists(pathResourceToContent);
            } catch (IOException ex) {
                log.error("Error to deleted the resource, cause: %s".formatted(e));
            }
            return Optional.empty();
        }

        // Retorna el identificador del recurso creado
        return Optional.of(resourceIdDto);
    }

    /**
     * Busca un recurso dado su identificador y lo retorna encapsulado en un `ResourceContentDto`.
     *
     * @param resourceId El identificador del recurso.
     * @return Un objeto `Optional` que contiene el contenido y los metadatos del recurso,
     *         o un `Optional.empty` si no se encuentra.
     */
    @Override
    public Optional<ResourceContentDto> findResource(UUID resourceId) {
        final String patternFileName = "%s".formatted(resourceId);
        final String patternFileJsonName = "%s.json".formatted(resourceId);

        final Optional<File> fileResourceOpt;
        final Optional<File> fileDescriptionResourceOpt;

        // Busca los archivos del recurso en el sistema
        final FilenameFilter filenameFilter = (dir, name) -> name.contains(patternFileName);
        final File[] files = storeConfigurationProperties.buildResourcePathFromRelativePathGivenNameResource()
                .toFile()
                .listFiles(filenameFilter);

        if (files == null || files.length <= 1) {
            log.debug("Error to find the resource with id {}, don't found", resourceId);
            return Optional.empty();
        }

        // Carga los archivos del recurso
        fileResourceOpt = Arrays.stream(files).filter(file -> file.getName().equalsIgnoreCase(patternFileName)).findFirst();
        fileDescriptionResourceOpt = Arrays.stream(files).filter(file -> file.getName().equalsIgnoreCase(patternFileJsonName)).findFirst();

        if (fileResourceOpt.isEmpty() || fileDescriptionResourceOpt.isEmpty()) {
            log.debug("Error the resource with id {}, uncompleted number files", resourceId);
            return Optional.empty();
        }

        // Lee el contenido y los metadatos del recurso
        try {
            final byte[] content = Files.readAllBytes(fileResourceOpt.get().toPath());

            final ResourceDescription resourceDescription = this.objectMapper
                    .readValue(fileDescriptionResourceOpt.get(), ResourceDescription.class);

            // Construye el resultado final
            final ResourceContentDto resourceContentDto = ResourceContentDto.builder()
                    .contentType(resourceDescription.getContentType())
                    .description(resourceDescription.getDescription())
                    .resourceName(resourceDescription.getResourceName())
                    .size(resourceDescription.getSize())
                    .content(content)
                    .resourceId(resourceId)
                    .build();

            return Optional.of(resourceContentDto);

        } catch (IOException e) {
            log.error("Error the read the file with id {}, because {}", resourceId, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Elimina un recurso (contenido y archivo de descripción) dado su identificador.
     *
     * @param resourceId El identificador del recurso.
     */
    @Override
    public void deleteResource(UUID resourceId) {
        final FilenameFilter filenameFilter = (dir, name) -> name.contains("%s".formatted(resourceId));
        final File[] filesByName = storeConfigurationProperties
                .buildResourcePathFromRelativePathGivenNameResource()
                .toFile()
                .listFiles(filenameFilter::accept);

        Arrays.stream(filesByName).forEach(file -> {
            try {
                Files.deleteIfExists(file.toPath());
            } catch (IOException e) {
                log.error("Error in deleteResource, cause: ".formatted(e));
            }
        });
    }
}