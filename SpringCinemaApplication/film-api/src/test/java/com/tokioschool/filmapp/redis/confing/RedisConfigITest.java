package com.tokioschool.filmapp.redis.confing;

import com.tokioschool.redis.confing.RedisConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import redis.embedded.RedisServer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {RedisConfig.class})
class RedisConfigITest {

    // redis server embedded
    private static RedisServer redisServer;

    @Autowired private LettuceConnectionFactory connectionFactory;
    @Autowired private RedisTemplate<String, Object> redisTemplate;


    @BeforeAll
    public static void setUp() throws Exception {
        redisServer = new RedisServer(6379);
        redisServer.start();
    }

    @AfterAll
    public static void tearDown() throws Exception {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

    @Test
    public void givenInstanceRedis_whenLettuceConnectionFactory_returnOk() {
        Assertions.assertThat(connectionFactory).isNotNull();
        Assertions.assertThat(connectionFactory.getHostName()).isEqualTo("localhost");
        Assertions.assertThat(connectionFactory.getPort()).isEqualTo(6379);
    }

    @Test
    public void givenInstanceRedis_whenRedisTemplateConnection_returnOk() {
        Assertions.assertThat(redisTemplate).isNotNull();
        Assertions.assertThat(redisTemplate.getConnectionFactory()).isNotNull();
    }
}