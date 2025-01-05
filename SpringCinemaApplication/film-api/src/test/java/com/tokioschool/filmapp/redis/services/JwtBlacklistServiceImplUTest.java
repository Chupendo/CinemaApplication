package com.tokioschool.filmapp.redis.services;

import com.tokioschool.redis.services.impl.JwtBlacklistServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JwtBlacklistServiceImplUTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private JwtBlacklistServiceImpl jwtBlacklistService;

    private static final String BLACKLIST_KEY = "blacklisted";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @Order(1)
    void givenToken_whenAddToBlacklist_thenReturnOk() {
        final long expiredAt = Instant.now().plus(3600, ChronoUnit.MILLIS).getEpochSecond();
        final String token = "token";

        jwtBlacklistService.addToBlacklist(token,expiredAt);

        Mockito.verify(redisTemplate.opsForValue(),Mockito.times(1))
                .set(token,BLACKLIST_KEY,expiredAt, TimeUnit.MILLISECONDS);
    }

    @Test
    @Order(2)
    void givenToken_whenIsBlacklisted_returnOk() {
        final String token = "token";

        Mockito.when(redisTemplate.hasKey(token)).thenReturn(Boolean.TRUE);

        boolean isTokenInBlacklisted = jwtBlacklistService.isBlacklisted(token);

        assertThat(isTokenInBlacklisted).isTrue();
        Mockito.verify(redisTemplate, Mockito.times(1)).hasKey(token);
    }
}