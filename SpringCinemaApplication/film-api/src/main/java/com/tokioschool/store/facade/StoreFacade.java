package com.tokioschool.store.facade;

import com.tokioschool.store.dto.ResourceContentDto;
import com.tokioschool.store.dto.ResourceIdDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

/**
 * Patrón de diseño "Fachada" para interactuar con un sistema cuya implementación es desconocida.
 *
 * Esta interfaz actúa como un cliente externo de la API de Store, proporcionando métodos para
 * registrar, guardar, buscar, eliminar y actualizar recursos.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public interface StoreFacade {

    /**
     * Registra un recurso en el sistema.
     *
     * @param multipartFile El archivo que representa el recurso.
     * @param description Una descripción del recurso.
     * @return Un {@link Optional} que contiene el identificador único del recurso registrado.
     */
    Optional<ResourceIdDto> registerResource(MultipartFile multipartFile, String description);

    /**
     * Guarda un recurso en el sistema. Requiere autenticación.
     *
     * @param multipartFile El archivo que representa el recurso.
     * @param description Una descripción del recurso.
     * @return Un {@link Optional} que contiene el identificador único del recurso guardado.
     */
    @PreAuthorize("isAuthenticated()")
    Optional<ResourceIdDto> saveResource(MultipartFile multipartFile, String description);

    /**
     * Busca un recurso en el sistema por su identificador. Requiere autenticación.
     *
     * @param resourceId El identificador único del recurso.
     * @return Un {@link Optional} que contiene el contenido del recurso si se encuentra.
     */
    @PreAuthorize("isAuthenticated()")
    Optional<ResourceContentDto> findResource(UUID resourceId);

    /**
     * Elimina un recurso del sistema por su identificador. Requiere autenticación.
     *
     * @param resourceId El identificador único del recurso a eliminar.
     */
    @PreAuthorize("isAuthenticated()")
    void deleteResource(UUID resourceId);

    /**
     * Actualiza un recurso existente en el sistema. Requiere autenticación.
     *
     * @param resourceIdOld El identificador único del recurso a actualizar.
     * @param multipartFile El nuevo archivo que representa el recurso.
     * @param description Una nueva descripción del recurso.
     * @return Un {@link Optional} que contiene el identificador único del recurso actualizado.
     */
    @PreAuthorize("isAuthenticated()")
    Optional<ResourceIdDto> updateResource(UUID resourceIdOld, MultipartFile multipartFile, String description);
}