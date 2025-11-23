package com.habittracker.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * Custom Health Indicator for Redis Connectivity
 *
 * Checks if Redis cache is reachable and responsive
 * Used by monitoring systems and load balancers
 */
@Component
public class RedisHealthIndicator implements HealthIndicator {

    private final RedisConnectionFactory redisConnectionFactory;

    public RedisHealthIndicator(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public Health health() {
        try {
            RedisConnection connection = redisConnectionFactory.getConnection();

            // Ping Redis to check connectivity
            String pong = connection.ping();
            connection.close();

            if ("PONG".equalsIgnoreCase(pong)) {
                return Health.up()
                        .withDetail("cache", "Redis")
                        .withDetail("status", "Connected")
                        .withDetail("ping", "Successful")
                        .build();
            } else {
                return Health.down()
                        .withDetail("cache", "Redis")
                        .withDetail("status", "Ping returned unexpected response")
                        .withDetail("response", pong)
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("cache", "Redis")
                    .withDetail("status", "Connection failed")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
