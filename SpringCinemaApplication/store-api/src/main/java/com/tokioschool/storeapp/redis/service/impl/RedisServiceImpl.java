package com.tokioschool.storeapp.redis.service.impl;

import com.tokioschool.storeapp.redis.dto.RedisDto;
import com.tokioschool.storeapp.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Created a register in redis as pair <key,value>
     * @param redisDto pair of id and value to saved
     */
    public void saveValue(RedisDto redisDto) {
        redisTemplate.opsForValue().set(redisDto.key(), redisDto.value());
    }

    /**
     * Method for recover the value as String given your key that hosting in the model of redis
     *
     * @param key filter for search the register
     * @return the value associated to key as String
     */
    public String getValue(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }
}
