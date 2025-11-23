package com.habittracker.exception;

/**
 * Exception thrown when a requested resource is not found
 *
 * Usage examples:
 * - User not found by ID
 * - Habit not found by ID
 * - Competition not found
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
