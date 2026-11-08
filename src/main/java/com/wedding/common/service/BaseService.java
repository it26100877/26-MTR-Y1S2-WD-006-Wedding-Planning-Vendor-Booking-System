package com.wedding.common.service;

/**
 * Abstract base service providing common business-logic scaffolding.
 * Concrete services extend this class to gain shared utilities.
 * Demonstrates Abstraction and Template Method pattern.
 *
 * @author Common Module
 */
public abstract class BaseService {

 /**
 * Validates that a string is not null/empty.
 * @throws com.wedding.common.exceptions.ValidationException if blank
 */
 protected void requireNonEmpty(String value, String fieldName) {
 if (value == null || value.trim().isEmpty()) {
 throw new com.wedding.common.exceptions.ValidationException(fieldName + " is required.");
 }
 }

 /**
 * Logs a service action to stdout (can be extended to a logger).
 */
 protected void log(String message) {
 System.out.println("[Service] " + getClass().getSimpleName() + " » " + message);
 }
}

