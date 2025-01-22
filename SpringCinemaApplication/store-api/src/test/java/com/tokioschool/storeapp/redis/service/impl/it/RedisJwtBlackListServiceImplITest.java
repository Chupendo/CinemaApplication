package com.tokioschool.storeapp.redis.service.impl.it;

import com.tokioschool.storeapp.redis.service.impl.RedisJwtBlackListServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RedisJwtBlackListServiceImplITest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private RedisJwtBlackListServiceImpl redisJwtBlackListService;

    private static final String BLACKLIST_KEY = "blacklisted";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @Order(1)
    void givenTokenAndTime_whenAddToBlacklist_thenReturnOk() {

        redisJwtBlackListService.addToBlacklist("token",1L);

        Mockito.verify(redisTemplate.opsForValue(),Mockito.times(1))
                .set("token",BLACKLIST_KEY,1L, TimeUnit.MILLISECONDS);
    }

    @Test
    @Order(2)
    void givenToken_whenIsBlacklisted_thenReturnTrue() {
        Mockito.when(redisTemplate.hasKey("token")).thenReturn(true);

        boolean result = redisJwtBlackListService.isBlacklisted("token");

        Mockito.verify(redisTemplate,Mockito.times(1))
                .hasKey("token");

        assertThat(result)
                .isTrue();
    }
}