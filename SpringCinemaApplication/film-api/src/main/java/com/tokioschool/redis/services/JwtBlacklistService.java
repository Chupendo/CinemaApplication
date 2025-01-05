package com.tokioschool.redis.services;

public interface JwtBlacklistService {
    void addToBlacklist(String token,long timeExpired);
    boolean isBlacklisted(String token);
}
