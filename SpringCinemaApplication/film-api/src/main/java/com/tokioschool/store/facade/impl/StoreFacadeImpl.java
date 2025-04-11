package com.tokioschool.store.facade.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokioschool.store.dto.ResourceContentDto;
import com.tokioschool.store.dto.ResourceIdDto;
import com.tokioschool.store.facade.StoreFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementación de la interfaz {@link StoreFacade}.
 *
 * Esta clase proporciona la lógica para interactuar con la API de Store, permitiendo
 * registrar, guardar, buscar, eliminar y actualizar recursos.
 *
 * Anotaciones:
 * - {@link Service}: Marca esta clase como un componente de servicio de Spring.
 * - {@link Slf4j}: Habilita el registro de logs utilizando SLF4J.
 * - {@link RequiredArgsConstructor}: Genera un constructor con los argumentos requeridos.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StoreFacadeImpl implements StoreFacade {

    @Qualifier("restClientConsumer")
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    private static final String RESOURCE_URL = "/store/api/resource";

    /**
     * Registra un recurso en el sistema.
     *
     * @param multipartFile El archivo que representa el recurso.
     * @param description Una descripción del recurso.
     * @return Un {@link Optional} que contiene el identificador único del recurso registrado.
     */
    @Override
    public Optional<ResourceIdDto> registerResource(MultipartFile multipartFile, String description) {
        // Simulación de la descripción como parte de la solicitud
        Map<String, String> descriptionMap = new HashMap<>();
        descriptionMap.put("description", description);
        String descriptionBody = StringUtils.EMPTY;
        try {
            descriptionBody = objectMapper.writeValueAsString(descriptionMap);
        } catch (JsonProcessingException e) {
            log.error("Error al procesar la descripción", e);
        }

        // Simulación del archivo como parte de la solicitud
        MediaType mediaType;
        try {
            mediaType = MediaType.valueOf(multipartFile.getContentType());
        } catch (Exception e) {
            log.error("Error al procesar el archivo", e);
            mediaType = MediaType.APPLICATION_OCTET_STREAM; // Se guarda como binario
        }

        // Construcción de las partes de la solicitud
        final HttpEntity<Object> descriptionPart = buildHttpEntity(MediaType.APPLICATION_JSON, descriptionBody);
        final HttpEntity<Object> resourcePart = buildHttpEntity(mediaType, multipartFile.getResource());

        // Montaje del cuerpo de la solicitud multipart
        MultiValueMap<Object, Object> parts = new LinkedMultiValueMap<>();
        parts.add("description", descriptionPart);
        parts.add("content", resourcePart);

        try {
            final ResourceIdDto resourceIdDto = restClient.post()
                    .uri(RESOURCE_URL)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(parts)
                    .retrieve()
                    .body(ResourceIdDto.class);

            return Optional.ofNullable(resourceIdDto);
        } catch (Exception e) {
            log.error("Error al guardar el recurso", e);
        }

        return Optional.empty();
    }

    /**
     * Guarda un recurso en el sistema.
     *
     * @param multipartFile El archivo que representa el recurso.
     * @param description Una descripción del recurso.
     * @return Un {@link Optional} que contiene el identificador único del recurso guardado.
     */
    @Override
    public Optional<ResourceIdDto> saveResource(MultipartFile multipartFile, String description) {
        // Simulación de la descripción como parte de la solicitud
        Map<String, String> descriptionMap = new HashMap<>();
        descriptionMap.put("description", description);
        String descriptionBody = StringUtils.EMPTY;
        try {
            descriptionBody = objectMapper.writeValueAsString(descriptionMap);
        } catch (JsonProcessingException e) {
            log.error("Error al procesar la descripción", e);
        }

        // Simulación del archivo como parte de la solicitud
        MediaType mediaType;
        try {
            mediaType = MediaType.valueOf(multipartFile.getContentType());
        } catch (Exception e) {
            log.error("Error al procesar el archivo", e);
            mediaType = MediaType.APPLICATION_OCTET_STREAM; // Se guarda como binario
        }

        // Construcción de las partes de la solicitud
        final HttpEntity<Object> descriptionPart = buildHttpEntity(MediaType.APPLICATION_JSON, descriptionBody);
        final HttpEntity<Object> resourcePart = buildHttpEntity(mediaType, multipartFile.getResource());

        // Montaje del cuerpo de la solicitud multipart
        MultiValueMap<Object, Object> parts = new LinkedMultiValueMap<>();
        parts.add("description", descriptionPart);
        parts.add("content", resourcePart);

        try {
            final ResourceIdDto resourceIdDto = restClient.post()
                    .uri(RESOURCE_URL)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(parts)
                    .retrieve()
                    .body(ResourceIdDto.class);

            return Optional.ofNullable(resourceIdDto);
        } catch (Exception e) {
            log.error("Error al guardar el recurso", e);
        }

        return Optional.empty();
    }

    /**
     * Busca un recurso en el sistema por su identificador.
     *
     * @param resourceId El identificador único del recurso.
     * @return Un {@link Optional} que contiene el contenido del recurso si se encuentra.
     */
    @Override
    public Optional<ResourceContentDto> findResource(UUID resourceId) {
        final String uri = "%s/{resourceId}".formatted(RESOURCE_URL);
        try {
            ResourceContentDto resourceContentDto = restClient.get()
                    .uri(uri, resourceId)
                    .retrieve()
                    .body(ResourceContentDto.class);

            return Optional.ofNullable(resourceContentDto);
        } catch (Exception e) {
            log.error("Excepción al buscar el recurso", e);
        }

        return Optional.empty();
    }

    /**
     * Elimina un recurso del sistema por su identificador.
     *
     * @param resourceId El identificador único del recurso a eliminar.
     */
    @Override
    public void deleteResource(UUID resourceId) {
        final String uri = "%s/{resourceId}".formatted(RESOURCE_URL);

        restClient.delete().uri(uri, resourceId)
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * Actualiza un recurso existente en el sistema.
     *
     * @param resourceIdOld El identificador único del recurso a actualizar.
     * @param multipartFile El nuevo archivo que representa el recurso.
     * @param description Una nueva descripción del recurso.
     * @return Un {@link Optional} que contiene el identificador único del recurso actualizado.
     */
    @Override
    @PreAuthorize("isAuthenticated()")
    public Optional<ResourceIdDto> updateResource(UUID resourceIdOld, MultipartFile multipartFile, String description) {
        Optional<ResourceIdDto> resourceIdDtoOptional = saveResource(multipartFile, description);

        if (resourceIdDtoOptional.isEmpty())
            return Optional.empty();

        if (resourceIdOld != null) {
            deleteResource(resourceIdOld);
        }

        return resourceIdDtoOptional;
    }

    /**
     * Construye una entidad HTTP con el tipo de contenido y el cuerpo proporcionados.
     *
     * @param mediaType El tipo de contenido de la entidad.
     * @param body El cuerpo de la entidad.
     * @return Una instancia de {@link HttpEntity}.
     */
    private HttpEntity<Object> buildHttpEntity(MediaType mediaType, Object body) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        return new HttpEntity<>(body, headers);
    }
}