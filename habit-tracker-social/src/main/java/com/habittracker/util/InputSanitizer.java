package com.habittracker.util;

import org.apache.commons.text.StringEscapeUtils;
import org.owasp.encoder.Encode;

import java.util.regex.Pattern;

/**
 * Utility class for sanitizing user input
 *
 * Provides methods to prevent XSS, SQL Injection, and other injection attacks
 * Uses OWASP Encoder and Apache Commons Text for comprehensive protection
 */
public class InputSanitizer {

    // Pattern to detect potential XSS attacks
    private static final Pattern XSS_PATTERN = Pattern.compile(
            "<script.*?>.*?</script>|javascript:|onerror=|onload=|<iframe|<object|<embed",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    // Pattern for SQL injection keywords
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            ".*(union|select|insert|update|delete|drop|create|alter|exec|execute|script|javascript|eval).*",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Sanitize HTML content to prevent XSS attacks
     * Encodes HTML special characters
     *
     * @param input Raw user input
     * @return Sanitized string safe for HTML output
     */
    public static String sanitizeHtml(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Encode.forHtml(input.trim());
    }

    /**
     * Sanitize for HTML attribute context
     *
     * @param input Raw user input
     * @return Sanitized string safe for HTML attributes
     */
    public static String sanitizeHtmlAttribute(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Encode.forHtmlAttribute(input.trim());
    }

    /**
     * Sanitize for JavaScript context
     *
     * @param input Raw user input
     * @return Sanitized string safe for JavaScript
     */
    public static String sanitizeJavaScript(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Encode.forJavaScript(input.trim());
    }

    /**
     * Sanitize for URL parameter context
     *
     * @param input Raw user input
     * @return Sanitized string safe for URL parameters
     */
    public static String sanitizeUrl(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Encode.forUriComponent(input.trim());
    }

    /**
     * General text sanitization (removes potentially dangerous content)
     * Suitable for database storage and API responses
     *
     * @param input Raw user input
     * @return Sanitized text
     */
    public static String sanitizeText(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String sanitized = input.trim();

        // Remove null bytes
        sanitized = sanitized.replaceAll("\0", "");

        // Escape HTML entities
        sanitized = StringEscapeUtils.escapeHtml4(sanitized);

        return sanitized;
    }

    /**
     * Strict sanitization for sensitive fields (usernames, emails)
     * Allows only alphanumeric, spaces, dots, hyphens, and underscores
     *
     * @param input Raw user input
     * @return Strictly sanitized string
     */
    public static String sanitizeStrict(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Allow only safe characters: alphanumeric, space, dot, hyphen, underscore
        return input.trim()
                .replaceAll("[^a-zA-Z0-9 .@_-]", "");
    }

    /**
     * Check if input contains potential XSS attack vectors
     *
     * @param input User input to check
     * @return true if potential XSS detected
     */
    public static boolean containsXss(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        return XSS_PATTERN.matcher(input).find();
    }

    /**
     * Check if input contains potential SQL injection
     * Note: This is just an additional check. Use parameterized queries (JPA does this)
     *
     * @param input User input to check
     * @return true if potential SQL injection detected
     */
    public static boolean containsSqlInjection(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        return SQL_INJECTION_PATTERN.matcher(input).matches();
    }

    /**
     * Validate and sanitize username
     * Rules: 3-50 characters, alphanumeric and underscore only
     *
     * @param username Raw username input
     * @return Sanitized username or throws IllegalArgumentException
     */
    public static String validateAndSanitizeUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        String sanitized = username.trim();

        if (sanitized.length() < 3 || sanitized.length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 and 50 characters");
        }

        if (!sanitized.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Username can only contain letters, numbers, and underscores");
        }

        return sanitized;
    }

    /**
     * Validate and sanitize email
     * Basic email format validation
     *
     * @param email Raw email input
     * @return Sanitized email or throws IllegalArgumentException
     */
    public static String validateAndSanitizeEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        String sanitized = email.trim().toLowerCase();

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!sanitized.matches(emailRegex)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (sanitized.length() > 100) {
            throw new IllegalArgumentException("Email too long");
        }

        return sanitized;
    }

    /**
     * Sanitize habit name or description
     * Allows alphanumeric, spaces, and common punctuation
     *
     * @param input Raw habit name/description
     * @return Sanitized text
     */
    public static String sanitizeHabitText(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String sanitized = input.trim();

        // Check for XSS
        if (containsXss(sanitized)) {
            throw new IllegalArgumentException("Input contains potentially dangerous content");
        }

        // Allow letters, numbers, spaces, and common punctuation
        sanitized = sanitized.replaceAll("[^a-zA-Z0-9 .,!?'-]", "");

        return sanitized;
    }

    /**
     * Truncate string to maximum length
     *
     * @param input Input string
     * @param maxLength Maximum allowed length
     * @return Truncated string
     */
    public static String truncate(String input, int maxLength) {
        if (input == null || input.length() <= maxLength) {
            return input;
        }
        return input.substring(0, maxLength);
    }

    /**
     * Remove all whitespace from string
     *
     * @param input Input string
     * @return String with whitespace removed
     */
    public static String removeWhitespace(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("\\s+", "");
    }

    /**
     * Normalize whitespace (replace multiple spaces with single space)
     *
     * @param input Input string
     * @return String with normalized whitespace
     */
    public static String normalizeWhitespace(String input) {
        if (input == null) {
            return null;
        }
        return input.trim().replaceAll("\\s+", " ");
    }
}
