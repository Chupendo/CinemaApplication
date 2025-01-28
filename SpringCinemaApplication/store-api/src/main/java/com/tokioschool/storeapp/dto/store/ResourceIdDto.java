package com.tokioschool.storeapp.dto.store;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ResourceIdDto(UUID resourceId) {
}
