package com.tokioschool.storeapp.service;

import com.tokioschool.storeapp.dto.store.ResourceContentDto;
import com.tokioschool.storeapp.dto.store.ResourceIdDto;
import jakarta.annotation.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

/**
 * Interfaz para el servicio de gestión de recursos.
 *
 * Esta interfaz define los metodos necesarios para gestionar recursos en el sistema,
 * incluyendo la carga, búsqueda y eliminación de recursos.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public interface StoreService {

    /**
     * Guarda un recurso en el sistema.
     *
     * Este metodo permite cargar un archivo al sistema, junto con una descripción opcional.
     * Solo puede ser accedido por usuarios autenticados.
     *
     * @param multipartFile El archivo que se desea cargar.
     * @param description Una descripción opcional del recurso.
     * @return Un objeto `Optional` que contiene el identificador del recurso encapsulado en `ResourceIdDto`.
     */
    @PreAuthorize("isAuthenticated()")
    Optional<ResourceIdDto> saveResource(MultipartFile multipartFile, @Nullable String description);

    /**
     * Busca un recurso en el sistema dado su identificador.
     *
     * Este metodo permite recuperar el contenido y los metadatos de un recurso.
     * Solo puede ser accedido por usuarios autenticados.
     *
     * @param resourceId El identificador único del recurso.
     * @return Un objeto `Optional` que contiene el recurso encapsulado en `ResourceContentDto`,
     *         o un `Optional.empty` si no se encuentra.
     */
    @PreAuthorize("isAuthenticated()")
    Optional<ResourceContentDto> findResource(UUID resourceId);

    /**
     * Elimina un recurso del sistema.
     *
     * Este metodo permite eliminar un recurso, incluyendo su contenido y metadatos,
     * dado su identificador. Solo puede ser accedido por usuarios autenticados.
     *
     * @param resourceId El identificador único del recurso a eliminar.
     */
    @PreAuthorize("isAuthenticated()")
    void deleteResource(UUID resourceId);
}