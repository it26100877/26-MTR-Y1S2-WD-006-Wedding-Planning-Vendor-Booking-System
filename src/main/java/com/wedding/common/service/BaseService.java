package com.wedding.common.service;

/**
 * Shared base for application services.
 * Provides common validation and logging helpers for concrete services.
 */
public abstract class BaseService {

    /** Ensures required text input is present. */
    protected void requireNonEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
    }

    /** Common lightweight service diagnostic hook. */
    protected void log(String message) {
        System.out.println("[Service] " + getClass().getSimpleName() + ": " + message);
    }
}
