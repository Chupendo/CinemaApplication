package com.tokioschool.filmapp.redis.services;

import com.tokioschool.redis.services.impl.RedisServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RedisServiceImplUTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks private RedisServiceImpl redisService;

    private static final String BLACKLIST_KEY = "blacklisted";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void givenPairKeyVale_whenSaveValue_thenReturnOk() {
        final String key = "key";

        redisService.saveValue(key,BLACKLIST_KEY);

        Mockito.verify(redisTemplate.opsForValue(),Mockito.times(1))
                .set(key,BLACKLIST_KEY);
    }

    @Test
    void givenKey_whenGetValue_thenReturnValue() {
        final String key = "key";
        final String value = "value";

        Mockito.when(redisTemplate.opsForValue().get(key)).thenReturn(value);

        String result = redisService.getValue(key);

        Mockito.verify(redisTemplate.opsForValue(),Mockito.times(1))
                .get(key);

        assertThat(result)
                .isNotNull()
                .isEqualTo(value);
    }
}