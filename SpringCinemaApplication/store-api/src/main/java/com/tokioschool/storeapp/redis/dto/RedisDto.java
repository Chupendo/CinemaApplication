package com.tokioschool.storeapp.redis.dto;

import lombok.Builder;

@Builder
public record RedisDto(String key,Object value) {
}
