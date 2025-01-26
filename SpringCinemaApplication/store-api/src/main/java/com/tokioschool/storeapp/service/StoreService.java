package com.tokioschool.storeapp.service;

import com.tokioschool.storeapp.dto.store.ResourceContentDto;
import com.tokioschool.storeapp.dto.store.ResourceIdDto;
import jakarta.annotation.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

public interface StoreService {
    @PreAuthorize("isAuthenticated()")
    Optional<ResourceIdDto> saveResource(MultipartFile multipartFile, @Nullable String description);

    @PreAuthorize("isAuthenticated()")
    Optional<ResourceContentDto> findResource(UUID resourceId);

    @PreAuthorize("isAuthenticated()")
    void deleteResource(UUID resourceId);
}
