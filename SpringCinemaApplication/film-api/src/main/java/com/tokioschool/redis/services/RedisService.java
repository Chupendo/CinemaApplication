package com.tokioschool.redis.services;

public interface RedisService {
    void saveValue(String clave, String valor);
    String getValue(String clave);
}
