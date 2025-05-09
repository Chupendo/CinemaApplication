package com.tokioschool.storeapp.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configuración de Redis para la aplicación.
 *
 * Esta clase define los beans necesarios para configurar la conexión a Redis
 * y el uso de RedisTemplate con serializadores personalizados.
 *
 * Notas:
 * - Utiliza Lettuce como cliente de conexión a Redis.
 * - Configura RedisTemplate para serializar claves y valores como cadenas.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
public class RedisConfiguration {

    /**
     * Crea una instancia de la fábrica de conexiones de Redis.
     *
     * @return una instancia de LettuceConnectionFactory, que proporciona una conexión nativa
     *         de un solo subproceso de forma predeterminada.
     */
    @Bean
    public LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory();
    }

    /**
     * Crea una instancia de RedisTemplate y la registra como un componente.
     *
     * Este bean requiere una instancia de RedisConnectionFactory, que será inyectada automáticamente
     * por Spring. Configura RedisTemplate para usar StringRedisSerializer tanto para las claves
     * como para los valores.
     *
     * @param connectionFactory instancia de RedisConnectionFactory inyectada automáticamente.
     * @return una instancia configurada de RedisTemplate.
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