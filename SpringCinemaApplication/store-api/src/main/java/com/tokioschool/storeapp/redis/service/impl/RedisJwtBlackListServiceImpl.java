package com.tokioschool.storeapp.redis.service.impl;

import com.tokioschool.storeapp.redis.service.RedisJwtBlackListService;
import com.tokioschool.storeapp.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisJwtBlackListServiceImpl implements RedisJwtBlackListService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_KEY = "blacklisted";
    /**
     * Created a register in redis as pair <key,value>, where key is the token
     * of user and value is the {@link #BLACKLIST_KEY}
     *
     * @param token value of black list
     * @param timeExpired time in MILLISECONDS of value in the redis
     */
    public void addToBlacklist(String token, long timeExpired) {
        redisTemplate.opsForValue().set(token, BLACKLIST_KEY, timeExpired, TimeUnit.MILLISECONDS);
    }

    /**
     * Verify if the token is the list black
     * @param token value to verify in the black list
     * @return true if the key is in redis system, otherwise false
     */
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }
}
