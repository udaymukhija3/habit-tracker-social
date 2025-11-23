package com.habittracker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter to apply rate limiting to API requests
 *
 * This filter intercepts requests and checks rate limits before
 * allowing them to proceed. Different endpoints have different limits:
 *
 * - /api/auth/** : 5 requests per 15 minutes (authentication endpoints)
 * - /api/** : 100 requests per minute (general API endpoints)
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    public RateLimitFilter(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String clientIp = getClientIP(request);
        String requestPath = request.getRequestURI();

        // Apply rate limiting based on endpoint
        boolean allowed = true;
        String limitType = "general";

        if (requestPath.startsWith("/api/auth/")) {
            // Stricter limit for authentication endpoints
            allowed = rateLimitService.tryConsumeAuth(clientIp);
            limitType = "auth";

            if (allowed) {
                // Add rate limit headers
                long remaining = rateLimitService.getAvailableAuthTokens(clientIp);
                addRateLimitHeaders(response, 5, remaining, 900); // 15 minutes = 900 seconds
            }
        } else if (requestPath.startsWith("/api/")) {
            // General API rate limit
            allowed = rateLimitService.tryConsumeApi(clientIp);
            limitType = "api";

            if (allowed) {
                long remaining = rateLimitService.getAvailableApiTokens(clientIp);
                addRateLimitHeaders(response, 100, remaining, 60); // 1 minute = 60 seconds
            }
        }

        if (!allowed) {
            // Rate limit exceeded
            handleRateLimitExceeded(response, limitType, clientIp);
            return;
        }

        // Continue with the request
        filterChain.doFilter(request, response);
    }

    /**
     * Get client IP address from request
     * Checks various headers for proxied requests
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0].trim();
        }

        String xrfHeader = request.getHeader("X-Real-IP");
        if (xrfHeader != null) {
            return xrfHeader;
        }

        return request.getRemoteAddr();
    }

    /**
     * Add rate limit headers to response
     * Following GitHub API rate limit header conventions
     */
    private void addRateLimitHeaders(
            HttpServletResponse response,
            long limit,
            long remaining,
            long resetSeconds
    ) {
        response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
        response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() / 1000 + resetSeconds));
    }

    /**
     * Handle rate limit exceeded response
     */
    private void handleRateLimitExceeded(
            HttpServletResponse response,
            String limitType,
            String clientIp
    ) throws IOException {

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");

        String message;
        long retryAfter;

        if ("auth".equals(limitType)) {
            message = "Too many authentication attempts. Please try again in 15 minutes.";
            retryAfter = 900; // 15 minutes
            response.setHeader("X-RateLimit-Limit", "5");
            response.setHeader("X-RateLimit-Remaining", "0");
        } else {
            message = "Too many requests. Please try again in 1 minute.";
            retryAfter = 60; // 1 minute
            response.setHeader("X-RateLimit-Limit", "100");
            response.setHeader("X-RateLimit-Remaining", "0");
        }

        response.setHeader("Retry-After", String.valueOf(retryAfter));
        response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() / 1000 + retryAfter));

        String jsonResponse = String.format(
            "{\"error\":\"Rate limit exceeded\",\"message\":\"%s\",\"retryAfter\":%d,\"clientIp\":\"%s\"}",
            message,
            retryAfter,
            maskIp(clientIp)
        );

        response.getWriter().write(jsonResponse);

        // Log the rate limit violation
        logger.warn(String.format(
            "Rate limit exceeded for IP: %s, Type: %s, Path: Rate limit filter",
            maskIp(clientIp),
            limitType
        ));
    }

    /**
     * Mask IP address for privacy (show only first two octets)
     * Example: 192.168.1.1 -> 192.168.*.*
     */
    private String maskIp(String ip) {
        if (ip == null || !ip.contains(".")) {
            return "***";
        }
        String[] parts = ip.split("\\.");
        if (parts.length == 4) {
            return parts[0] + "." + parts[1] + ".*.*";
        }
        return "***";
    }

    /**
     * Skip rate limiting for static resources and health checks
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.equals("/api/health") ||
               path.startsWith("/static/") ||
               path.startsWith("/public/");
    }
}
