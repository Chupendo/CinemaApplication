package com.tokioschool.redis.services.impl;

import com.tokioschool.redis.services.JwtBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JwtBlacklistServiceImpl implements JwtBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_KEY = "blacklisted";

    public void addToBlacklist(String token, long timeExpired) {
        redisTemplate.opsForValue().set(token, BLACKLIST_KEY, timeExpired, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }
}
