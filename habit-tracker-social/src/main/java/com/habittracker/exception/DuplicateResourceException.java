package com.habittracker.exception;

/**
 * Exception thrown when attempting to create a resource that already exists
 *
 * Usage examples:
 * - Username already taken
 * - Email already registered
 * - Duplicate habit name for user
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue));
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
