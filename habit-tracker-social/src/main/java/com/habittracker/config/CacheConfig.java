package com.habittracker.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis Caching Configuration
 * 
 * Configures Redis as the caching layer for frequently accessed data
 * Provides significant performance improvements for read-heavy operations
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure Redis Cache Manager with custom TTL settings
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // Default TTL: 1 hour
                .entryTtl(Duration.ofHours(1))
                // Serialize keys as strings
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // Serialize values as JSON
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()))
                // Don't cache null values
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                // Custom TTL for specific caches
                .withCacheConfiguration("users", config.entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration("habits", config.entryTtl(Duration.ofMinutes(15)))
                .withCacheConfiguration("friendships", config.entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration("notifications", config.entryTtl(Duration.ofMinutes(5)))
                .build();
    }

    /**
     * Redis Template for manual cache operations
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Use String serializer for keys
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Use JSON serializer for values
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}
