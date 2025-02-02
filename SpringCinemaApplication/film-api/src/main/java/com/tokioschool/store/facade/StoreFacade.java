package com.tokioschool.store.facade;

import com.tokioschool.store.dto.ResourceContentDto;
import com.tokioschool.store.dto.ResourceIdDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

/**
 * Patron de diseño "Fachado" para intercuta con algo que se desconece como esta implementado,
 * ya que esta api es un cliente exteno de Store
 */
public interface StoreFacade {

    Optional<ResourceIdDto> saveResource(MultipartFile multipartFile, String description);
    Optional<ResourceContentDto> findResource(UUID resourceId);
    void deleteResource(UUID resourceId);
}
