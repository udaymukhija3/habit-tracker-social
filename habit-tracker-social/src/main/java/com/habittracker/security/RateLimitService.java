package com.habittracker.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing API rate limiting using Token Bucket algorithm
 *
 * Rate Limits:
 * - Authentication endpoints (/api/auth/*): 5 requests per 15 minutes
 * - General API endpoints: 100 requests per minute
 * - Heavy operations: 10 requests per minute
 */
@Service
public class RateLimitService {

    // Cache to store buckets per IP address
    private final Cache<String, Bucket> authCache;
    private final Cache<String, Bucket> apiCache;
    private final Cache<String, Bucket> heavyOperationCache;

    public RateLimitService() {
        // Cache for auth endpoints (expires after 15 minutes)
        this.authCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(15))
                .maximumSize(10000)
                .build();

        // Cache for general API endpoints (expires after 1 minute)
        this.apiCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(1))
                .maximumSize(10000)
                .build();

        // Cache for heavy operations (expires after 1 minute)
        this.heavyOperationCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(1))
                .maximumSize(10000)
                .build();
    }

    /**
     * Resolve bucket for authentication endpoints
     * Limit: 5 requests per 15 minutes
     */
    public Bucket resolveAuthBucket(String key) {
        return authCache.get(key, k -> createAuthBucket());
    }

    /**
     * Resolve bucket for general API endpoints
     * Limit: 100 requests per minute
     */
    public Bucket resolveApiBucket(String key) {
        return apiCache.get(key, k -> createApiBucket());
    }

    /**
     * Resolve bucket for heavy operations (e.g., bulk operations, exports)
     * Limit: 10 requests per minute
     */
    public Bucket resolveHeavyOperationBucket(String key) {
        return heavyOperationCache.get(key, k -> createHeavyOperationBucket());
    }

    /**
     * Create bucket for authentication endpoints
     * 5 requests per 15 minutes = 1 request every 3 minutes
     */
    private Bucket createAuthBucket() {
        Bandwidth limit = Bandwidth.classic(
                5, // capacity: 5 tokens
                Refill.intervally(5, Duration.ofMinutes(15)) // refill 5 tokens every 15 minutes
        );
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Create bucket for general API endpoints
     * 100 requests per minute
     */
    private Bucket createApiBucket() {
        Bandwidth limit = Bandwidth.classic(
                100, // capacity: 100 tokens
                Refill.intervally(100, Duration.ofMinutes(1)) // refill 100 tokens every minute
        );
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Create bucket for heavy operations
     * 10 requests per minute
     */
    private Bucket createHeavyOperationBucket() {
        Bandwidth limit = Bandwidth.classic(
                10, // capacity: 10 tokens
                Refill.intervally(10, Duration.ofMinutes(1)) // refill 10 tokens every minute
        );
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Try to consume a token from authentication bucket
     *
     * @param key Unique identifier (usually IP address)
     * @return true if request is allowed, false if rate limit exceeded
     */
    public boolean tryConsumeAuth(String key) {
        Bucket bucket = resolveAuthBucket(key);
        return bucket.tryConsume(1);
    }

    /**
     * Try to consume a token from API bucket
     *
     * @param key Unique identifier (usually IP address)
     * @return true if request is allowed, false if rate limit exceeded
     */
    public boolean tryConsumeApi(String key) {
        Bucket bucket = resolveApiBucket(key);
        return bucket.tryConsume(1);
    }

    /**
     * Try to consume a token from heavy operation bucket
     *
     * @param key Unique identifier (usually IP address)
     * @return true if request is allowed, false if rate limit exceeded
     */
    public boolean tryConsumeHeavyOperation(String key) {
        Bucket bucket = resolveHeavyOperationBucket(key);
        return bucket.tryConsume(1);
    }

    /**
     * Get available tokens for authentication bucket
     *
     * @param key Unique identifier
     * @return number of available tokens
     */
    public long getAvailableAuthTokens(String key) {
        Bucket bucket = resolveAuthBucket(key);
        return bucket.getAvailableTokens();
    }

    /**
     * Get available tokens for API bucket
     *
     * @param key Unique identifier
     * @return number of available tokens
     */
    public long getAvailableApiTokens(String key) {
        Bucket bucket = resolveApiBucket(key);
        return bucket.getAvailableTokens();
    }

    /**
     * Clear all rate limit caches (for testing or admin purposes)
     */
    public void clearAllCaches() {
        authCache.invalidateAll();
        apiCache.invalidateAll();
        heavyOperationCache.invalidateAll();
    }

    /**
     * Clear rate limit for specific key (for admin purposes)
     *
     * @param key Unique identifier to clear
     */
    public void clearRateLimit(String key) {
        authCache.invalidate(key);
        apiCache.invalidate(key);
        heavyOperationCache.invalidate(key);
    }
}
