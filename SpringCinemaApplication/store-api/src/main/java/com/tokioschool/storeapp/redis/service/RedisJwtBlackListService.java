package com.tokioschool.storeapp.redis.service;

public interface RedisJwtBlackListService {
    void addToBlacklist(String token,long timeExpired);
    boolean isBlacklisted(String token);
}
