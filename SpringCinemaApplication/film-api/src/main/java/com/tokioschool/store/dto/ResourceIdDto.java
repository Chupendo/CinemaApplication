package com.tokioschool.store.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ResourceIdDto(UUID resourceId) {
}
