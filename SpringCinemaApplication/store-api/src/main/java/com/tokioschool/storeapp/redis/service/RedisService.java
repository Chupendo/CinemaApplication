package com.tokioschool.storeapp.redis.service;

import com.tokioschool.storeapp.redis.dto.RedisDto;

public interface RedisService {
    void saveValue(RedisDto redisDto);
    String getValue(String clave);
}
