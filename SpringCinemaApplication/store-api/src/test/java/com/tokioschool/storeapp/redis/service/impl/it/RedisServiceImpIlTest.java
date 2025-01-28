package com.tokioschool.storeapp.redis.service.impl.it;

import com.tokioschool.storeapp.redis.dto.RedisDto;
import com.tokioschool.storeapp.redis.service.impl.RedisServiceImpl;
import org.junit.jupiter.api.*;
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
class RedisServiceImpIlTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private RedisServiceImpl redisService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @Order(1)
    void givenRedisDto_whenSaveValue_thenReturnOk() {
        final RedisDto redisDto = new RedisDto("value","test");

        redisService.saveValue(redisDto);

        Mockito.verify(redisTemplate.opsForValue(),Mockito.times(1))
                .set(redisDto.key(),redisDto.value());
    }

    @Test
    @Order(2)
    void givenKey_whenGetValue_thenReturnValue() {
        final RedisDto redisDto = new RedisDto("value","test");

        Mockito.when(redisTemplate.opsForValue().get(redisDto.key())).thenReturn(redisDto.value());

        String result = redisService.getValue(redisDto.key());

        Mockito.verify(redisTemplate.opsForValue(),Mockito.times(1))
                .get(redisDto.key());

        assertThat(result)
                .isNotNull()
                .isEqualTo(redisDto.value());
    }
}