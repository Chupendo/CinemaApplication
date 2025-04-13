package com.tokioschool.redis.confing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Clase de configuración para Redis.
 *
 * Esta clase define los beans necesarios para configurar la conexión y el uso de Redis
 * en la aplicación utilizando Spring Data Redis y Lettuce como cliente.
 *
 * Anotaciones:
 * - {@link Configuration}: Indica que esta clase contiene definiciones de beans para el contexto de Spring.
 * - {@link Bean}: Marca un metodo como productor de un bean administrado por Spring.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
public class RedisConfig {

    /**
     * Crea un bean de tipo {@link LettuceConnectionFactory}.
     *
     * Este bean se utiliza para establecer la conexión con el servidor Redis
     * utilizando Lettuce como cliente.
     *
     * @return una instancia de {@link LettuceConnectionFactory}.
     */
    @Bean
    public LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory();
    }

    /**
     * Crea un bean de tipo {@link RedisTemplate}.
     *
     * Este bean se utiliza para realizar operaciones con Redis. Configura
     * serializadores para las claves y los valores, utilizando {@link StringRedisSerializer}.
     *
     * @param connectionFactory la fábrica de conexiones de Redis.
     * @return una instancia configurada de {@link RedisTemplate}.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}